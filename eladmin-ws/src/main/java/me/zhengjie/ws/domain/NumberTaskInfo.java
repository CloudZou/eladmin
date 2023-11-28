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
package me.zhengjie.ws.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author eladmin
* @date 2023-11-28
**/
@Entity
@Data
@Table(name="number_task_info")
public class NumberTaskInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "`task_name`")
    @ApiModelProperty(value = "taskName")
    private String taskName;

    @Column(name = "`phone_num`")
    @ApiModelProperty(value = "phoneNum")
    private String phoneNum;

    @Column(name = "`sms_url_str`")
    @ApiModelProperty(value = "smsUrlStr")
    private String smsUrlStr;

    @Column(name = "`reason_str`")
    @ApiModelProperty(value = "reasonStr")
    private String reasonStr;

    @Column(name = "`wait_time`")
    @ApiModelProperty(value = "waitTime")
    private Integer waitTime;

    @Column(name = "`run_timestamp`")
    @ApiModelProperty(value = "runTimestamp")
    private Long runTimestamp;

    @Column(name = "`protocols`")
    @ApiModelProperty(value = "protocols")
    private String protocols;

    @Column(name = "`number_type`")
    @ApiModelProperty(value = "numberType")
    private String numberType;


    @Column(name = "`start_time`")
    @ApiModelProperty(value = "startTime")
    private Timestamp startTime;

    @Column(name = "`end_time`")
    @ApiModelProperty(value = "endTime")
    private Timestamp endTime;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    public void copy(NumberTaskInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
