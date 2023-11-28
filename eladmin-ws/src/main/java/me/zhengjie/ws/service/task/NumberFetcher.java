package me.zhengjie.ws.service.task;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.ws.domain.NumberTaskInfo;
import org.springframework.stereotype.Service;

/**
 * 通过API或者firefox方式获取号码
 */
@Slf4j
@Service
public class NumberFetcher {
    /**
     * 根据api号码分组获取号码信息
     * @param apiGroupId
     * @return
     */
    public static NumberTaskInfo getNumberTaskFromApi(String apiGroupId){
        NumberTaskInfo numberTaskInfo = new NumberTaskInfo();
        return numberTaskInfo;
    }

    /**
     * 从firefox获取号码和收码链接
     * @param token
     * @return
     */
    public static NumberTaskInfo getNumberTaskFromFirefox(String token) {
        NumberTaskInfo numberTaskInfo = new NumberTaskInfo();
        return numberTaskInfo;
    }
}
