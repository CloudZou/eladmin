package me.zhengjie.ws.service.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsRetryTaskPolicy {
    /**
     * 下次重试时间间隔
     */
    private Integer waitTimeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试手机号码
     */
    private String phoneNum;

    /**
     * 代理配置
     */
    private String proxy;

}
