package me.zhengjie.ws.service.task;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ws二次卡批量任务
 */
@Slf4j
@AllArgsConstructor
@Service
public class WsTaskRequest {
    public String sendRegisterRequest(String cc, String phoneNum, WsRetryTaskPolicy policy) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cc", cc);
        requestBody.put("phone", phoneNum.substring(phoneNum.indexOf(cc)));
        requestBody.put("mcc", "310");
        requestBody.put("mnc", "030");
        requestBody.put("proxy", policy.getProxy());
        requestBody.put("method", "sms");
        String registerAPIUrl = "http://47.236.121.146:15800/api/v1/registration/register";
        return postRequestWithRetryPolicy(registerAPIUrl, requestBody, policy);
    }

    public String sendVerifySmsCodeRequest(String cc, String code, String phoneNum, WsRetryTaskPolicy policy) throws Exception{
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cc", cc);
        requestBody.put("phone", phoneNum.substring(phoneNum.indexOf(cc)));
        requestBody.put("mcc", "310");
        requestBody.put("mnc", "030");
        requestBody.put("code", code);
        requestBody.put("proxy", policy.getProxy());
        requestBody.put("method", "sms");
        String resisterVerifyUrl = "http://47.236.121.146:15800/api/v1/registration/verify";
        return postRequestWithRetryPolicy(resisterVerifyUrl, requestBody, policy);
    }
    private String postRequestWithRetryPolicy(String url, Map<String, String> requestBody, WsRetryTaskPolicy policy) throws Exception{
        Retryer<String> retryer = getStringRetryer(policy);
        return retryer.call(() -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.get("application/json"), JSONObject.toJSONString(requestBody))).build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody body = response.body();
                if (response.isSuccessful()) {
                    log.info("手机号码:{},success:{}", policy.getPhoneNum(), body == null ? "" : body.string());
                    if (body.toString().isEmpty()) {
                        log.error("接口调用时，返回结果出错:{}", policy.getPhoneNum());
                    }
                    return body.string();
                } else {
                    log.error("手机号码:{}, error,statusCode={},body={}", policy.getPhoneNum(), response.code(), body == null ? "" : body.string());
                    throw new Exception(String.format("手机号码:%s 调用失败", policy.getPhoneNum()));
                }
            }
        });
    }

    private Retryer<String> getStringRetryer(WsRetryTaskPolicy policy) {
        return RetryerBuilder.<String>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(policy.getWaitTimeout(), TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(policy.getRetryCount()))
                .withRetryListener(new RetryLogListener(policy)).build();
    }


    public String getSmsCodeWithRetryPolicy(String url, WsRetryTaskPolicy policy) throws Exception {
        Retryer<String> retryer = getStringRetryer(policy);

        return retryer.call(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            HttpUrl httpUrl = HttpUrl.parse(url).newBuilder().build();

            Request request = new Request.Builder().url(httpUrl.toString()).build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody body = response.body();
                if (response.isSuccessful()) {
                    Pattern pattern = Pattern.compile("(\\b\\d{3}-\\d{3}\\b)|(\\b\\d{3}\\d{3}\\b)");
                    Matcher matcher = pattern.matcher(body.string());
                    if (matcher.find()) {
                        String code = matcher.group(1).replace("-", "");
                        log.info("手机号码:{}, 获取到验证码: {}", policy.getPhoneNum(), code);
                        return code;
                    } else {
                        throw new Exception(String.format("手机号码:%s 未收到验证码", policy.getPhoneNum()));
                    }
                } else {
                    log.error("手机号码:{}, error,statusCode={},body={}", policy.getPhoneNum(), response.code(), body == null ? "" : body.string());
                    throw new Exception(String.format("手机号码:%s 未收到验证码", policy.getPhoneNum()));
                }
            }
        });
    }
}
