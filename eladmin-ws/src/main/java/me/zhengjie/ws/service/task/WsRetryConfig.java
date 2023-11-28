package me.zhengjie.ws.service.task;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class WsRetryConfig {
    private Integer Block_Wait_Second = 600;

    private Integer Wait_Verify_Code_Second = 5;

    private Integer Retry_Verify_Code_Count = 6;

    private Integer Cannot_Get_VerifyCode_Wait_Second = 600;

    private Integer Too_Recent_Wait_Second = 600;

    private Integer UnknownError_Wait_Second = 600;

    private Integer ProxyError_Count = 10;

    private Integer BadParam_Wait_Second = 50;
}
