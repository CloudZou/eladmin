package me.zhengjie.ws.service.task;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.ws.domain.NumberTaskInfo;
import me.zhengjie.ws.domain.WsTask;
import me.zhengjie.ws.service.NumberTaskInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RequiredArgsConstructor
public class WsTaskManager {
    private final WsTaskRequest wsTaskRequest;
    private final WsRetryConfig wsRetryConfig;
    private final NumberTaskInfoService numberTaskInfoService;
    private int successCount = 0;

    private ThreadPoolTaskExecutor mainThreadPoolExecutor;

    public HashMap<String, Integer> getWsTaskConfig(){
        HashMap<String, Integer> errorReasonRetryMap = new HashMap<String, Integer>();
        errorReasonRetryMap.put("blocked", wsRetryConfig.getBlock_Wait_Second());
        errorReasonRetryMap.put("too_recent", wsRetryConfig.getToo_Recent_Wait_Second());
        errorReasonRetryMap.put("bad_param", wsRetryConfig.getBadParam_Wait_Second());
        errorReasonRetryMap.put("too_many", wsRetryConfig.getToo_Recent_Wait_Second());
        errorReasonRetryMap.put("unknown", wsRetryConfig.getUnknownError_Wait_Second());
        return errorReasonRetryMap;
    }
    private static ConcurrentLinkedQueue failedLinkedQueue = new ConcurrentLinkedQueue<NumberTaskInfo>();

    public ThreadPoolTaskExecutor wsAsyncExecutor(int threadCount) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setQueueCapacity(1024 * 100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("ws-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    public void start(WsTask wsTask) {
        this.mainThreadPoolExecutor = wsAsyncExecutor(wsTask.getThreadCount());
        while (true) {
            try {
                if (successCount > wsTask.getNumberCount()) {
                    this.mainThreadPoolExecutor.shutdown();
                    return;
                }
                NumberTaskInfo numberTaskInfo = null;
                if (wsTask.getNumberType().equals("api")) {
                    numberTaskInfo = NumberFetcher.getNumberTaskFromApi(wsTask.getNumberExtra());
                }
                if (wsTask.getNumberType().equals("firefox")) {
                    numberTaskInfo = NumberFetcher.getNumberTaskFromFirefox(wsTask.getNumberExtra());
                }
                numberTaskInfo.setWaitTime(0);
                numberTaskInfo.setReasonStr("");
                numberTaskInfo.setRunTimestamp(System.currentTimeMillis());
                numberTaskInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
                numberTaskInfo.setTaskName(wsTask.getName());
                numberTaskInfo.setNumberType(wsTask.getNumberType());
                numberTaskInfoService.initNumberTaskInfo(numberTaskInfo);

                NumberTaskInfo finalNumberTaskInfo = numberTaskInfo;
                //代理信息
                WsRetryTaskPolicy policy = new WsRetryTaskPolicy(1, 10, numberTaskInfo.getPhoneNum(), "");
                this.mainThreadPoolExecutor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        doRegister(finalNumberTaskInfo, policy);
                        return null;
                    }
                });
                if (!failedLinkedQueue.isEmpty()) {
                    NumberTaskInfo failedNumberTaskInfo = (NumberTaskInfo) failedLinkedQueue.poll();
                    if (failedNumberTaskInfo.getReasonStr().equals("blocked") || failedNumberTaskInfo.getReasonStr().equals("no_routes") ||
                            failedNumberTaskInfo.getReasonStr().equals("unknown") || failedNumberTaskInfo.getReasonStr().equals("too_many") ||
                            failedNumberTaskInfo.getReasonStr().equals("too_recent")) {
                        long timeDiff = System.currentTimeMillis() - failedNumberTaskInfo.getRunTimestamp();
                        if (timeDiff < failedNumberTaskInfo.getWaitTime()) {
                            //  时间没有到，继续放回队列
                            failedLinkedQueue.offer(failedNumberTaskInfo);
                        } else {
                            this.mainThreadPoolExecutor.submit(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    doRegisterRetry(failedNumberTaskInfo, policy);
                                    return null;
                                }
                            });

                        }
                    }
                }
                Thread.sleep(20);
            }catch (Exception ex) {

            }
        }
    }

    private void stop(){
        try {
            this.mainThreadPoolExecutor.shutdown();
        }catch (Exception ex) {
            log.error("停止线程池任务失败,{}", ex);
        }
    }

    private void doRegister(NumberTaskInfo numberTaskInfo, WsRetryTaskPolicy policy) {
        try {
            if (registerOne(numberTaskInfo, policy)) {
                log.info("【新号码】{} 发送注册请求成功！！下一步获取验证码并提交验证！", numberTaskInfo.getPhoneNum());
                VerifySmsCodeResponse verifySmsCodeResponse = verifySmsCode(numberTaskInfo, policy);
                if (verifySmsCodeResponse != null && verifySmsCodeResponse.getStatus().equals("success")) {
                    numberTaskInfo.setProtocols(verifySmsCodeResponse.getProtocals());

                    numberTaskInfoService.update(numberTaskInfo);
                    successCount ++;
                }
            }
        }catch (Exception ex) {
            log.error("新号码 {} 执行任务异常,{}",numberTaskInfo.getPhoneNum(), ex);
        }
    }

    private void doRegisterRetry(NumberTaskInfo numberTaskInfo, WsRetryTaskPolicy policy) {
        try {
            if (registerOne(numberTaskInfo, policy)) {
                log.info("【重试号码】{} 发送注册请求成功！下一步获取验证码并提交验证！", numberTaskInfo.getPhoneNum());
                VerifySmsCodeResponse verifySmsCodeResponse = verifySmsCode(numberTaskInfo, policy);
                if (verifySmsCodeResponse != null && verifySmsCodeResponse.getStatus().equals("success")) {
                    numberTaskInfo.setProtocols(verifySmsCodeResponse.getProtocals());

                    numberTaskInfoService.update(numberTaskInfo);
                    successCount ++;
                }
            }
        }catch (Exception ex) {
            log.error("重试号码 {} 执行任务异常,{}",numberTaskInfo.getPhoneNum(), ex);
        }
    }


    private VerifySmsCodeResponse verifySmsCode(NumberTaskInfo numberTaskInfo, WsRetryTaskPolicy policy) {
        try {
            String code = wsTaskRequest.getSmsCodeWithRetryPolicy(numberTaskInfo.getSmsUrlStr(), policy);
            if (StringUtils.isEmpty(code)) {
                String reason = "no_sms";
                // 这里要根据是否firefox,如果是firefox要释放手机号，并且不需要重试
                numberTaskInfo.setRunTimestamp(System.currentTimeMillis());
                numberTaskInfo.setWaitTime(wsRetryConfig.getCannot_Get_VerifyCode_Wait_Second());
                numberTaskInfo.setReasonStr(reason);

                numberTaskInfoService.update(numberTaskInfo);
                failedLinkedQueue.add(numberTaskInfo);
                return null;
            } else {
                String body = wsTaskRequest.sendVerifySmsCodeRequest("1", code, numberTaskInfo.getPhoneNum(), policy);
                VerifySmsCodeResponse verifySmsCodeResponse = JSONObject.parseObject(body, VerifySmsCodeResponse.class);
                if (verifySmsCodeResponse.getStatus().equals("error")) {
                    log.error("手机号{} 验证短信时返回结果失败,{}", body);
                    return null;
                } else if (verifySmsCodeResponse.getStatus().equals("success")) {
                    return verifySmsCodeResponse;
                } else {
                    log.error("手机号{} 验证短信时进入未知异常,{}", body);
                    return null;
                }
            }

        }catch (Exception ex) {
            String reason = "no_sms";
            numberTaskInfo.setRunTimestamp(System.currentTimeMillis());
            numberTaskInfo.setReasonStr(reason);

            numberTaskInfoService.update(numberTaskInfo);
            failedLinkedQueue.add(numberTaskInfo);
            return null;
        }
    }

    /**
     * 发送手机号注册请求，并根据结果是否返回或者加入到失败队列
     */
    private boolean registerOne(NumberTaskInfo numberTaskInfo, WsRetryTaskPolicy policy) throws Exception{
        String body = wsTaskRequest.sendRegisterRequest("1", numberTaskInfo.getPhoneNum(), policy);
        SendRegisterResponse registerResponse = JSONObject.parseObject(body, SendRegisterResponse.class);
        if (registerResponse.getStatus().equals("error")) {
            if (registerResponse.getReason().equals("no_routes")){
                Integer smsWait = registerResponse.getSms_wait();
                numberTaskInfo.setWaitTime(smsWait);
            } else {
                if (getWsTaskConfig().containsKey(registerResponse.getReason())) {
                    numberTaskInfo.setWaitTime(getWsTaskConfig().get(registerResponse.getReason()));
                }else {
                    numberTaskInfo.setWaitTime(getWsTaskConfig().get("unknown"));
                }
            }
            numberTaskInfo.setRunTimestamp(System.currentTimeMillis());
            numberTaskInfo.setReasonStr(registerResponse.getReason());

            numberTaskInfoService.update(numberTaskInfo);
            failedLinkedQueue.add(numberTaskInfo);
            return false;
        }
        if (registerResponse.getStatus().equals("success")) {
            return true;
        }
        return false;

    }
}
