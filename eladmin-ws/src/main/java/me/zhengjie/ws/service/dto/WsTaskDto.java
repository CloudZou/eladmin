/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.ws.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author eladmin
* @date 2023-11-25
**/
@Data
public class WsTaskDto implements Serializable {

    private Integer id;

    /** 名称 */
    private String name;

    private Integer proxyGroupId;

    /** 代理分组 */
    private String proxyGroupName;

    /** 国家代码 */
    private String countryCode;

    /** 国家范围 */
    private String countryRange;

    /** 指定国家 */
    private String countryList;

    /** 接码平台 */
    private String numberType;

    /** 接码配置 */
    private String numberExtra;

    /** 接码平台 */
    private String codeType;

    /** 账号类型 */
    private String accountType;

    /** 线程数量 */
    private Integer threadCount;

    /** 验证码等待时长 */
    private Integer codeWaitTime;

    private Integer numberCount;

    /** 创建时间 */
    private Timestamp createTime;

    private Timestamp updateTime;

    /** 是否关闭二次验证 */
    private Boolean f2aOption;
}