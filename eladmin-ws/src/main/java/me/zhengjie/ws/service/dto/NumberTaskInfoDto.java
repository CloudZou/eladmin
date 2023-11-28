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
* @date 2023-11-28
**/
@Data
public class NumberTaskInfoDto implements Serializable {

    private Integer id;

    private String taskName;

    private String phoneNum;

    private String smsUrlStr;

    private String reasonStr;

    private Integer waitTime;

    private Long runTimestamp;

    private String protocols;

    private String numberType;

    private Timestamp startTime;

    private Timestamp endTime;

    private Timestamp updateTime;
}