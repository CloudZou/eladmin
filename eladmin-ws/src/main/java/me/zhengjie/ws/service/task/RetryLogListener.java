package me.zhengjie.ws.service.task;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryLogListener implements RetryListener {
    private WsRetryTaskPolicy policy;
    public RetryLogListener(WsRetryTaskPolicy policy) {
        this.policy = policy;
    }
    @Override
    public <V> void onRetry(Attempt<V> attempt) {
        // 第几次重试,(注意:第一次重试其实是第一次调用)
        log.info("手机号码: {}, retry time : [{}]",policy.getPhoneNum(), attempt.getAttemptNumber());
        // 距离第一次重试的延迟
        log.info("手机号码: {}, retry delay : [{}]", policy.getPhoneNum(), attempt.getDelaySinceFirstAttempt());
        // 重试结果: 是异常终止, 还是正常返回
        log.info("手机号码: {}, hasException={}", policy.getPhoneNum(), attempt.hasException());
        log.info("手机号码: {}, hasResult={}", policy.getPhoneNum(), attempt.hasResult());
        // 是什么原因导致异常
        if (attempt.hasException()) {
            log.info("手机号码: {}, causeBy={}" , policy.getPhoneNum(), attempt.getExceptionCause().toString());
        } else {
            // 正常返回时的结果
            log.info("手机号码: {}, result={}" , policy.getPhoneNum(), attempt.getResult());
        }
        log.info("手机号码: {}, log listen over.",policy.getPhoneNum());
    }
}