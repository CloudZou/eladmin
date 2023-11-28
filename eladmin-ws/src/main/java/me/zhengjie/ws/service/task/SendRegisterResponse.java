package me.zhengjie.ws.service.task;

import lombok.Data;

@Data
public class SendRegisterResponse {
    private String status;
    private String reason;
    private Integer length;
    private String login;
    private Integer sms_wait;
}
