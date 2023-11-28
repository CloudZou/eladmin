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
* @date 2023-11-25
**/
@Entity
@Data
@Table(name="ws_number_history")
public class WsNumberHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "`group_id`")
    @ApiModelProperty(value = "groupId")
    private Integer groupId;

    @Column(name = "`group_name`")
    @ApiModelProperty(value = "groupName")
    private String groupName;

    @Column(name = "`number`")
    @ApiModelProperty(value = "number")
    private String number;

    @Column(name = "`start_time`")
    @ApiModelProperty(value = "startTime")
    private Timestamp startTime;

    @Column(name = "`end_time`")
    @ApiModelProperty(value = "endTime")
    private Timestamp endTime;

    @Column(name = "`status`")
    @ApiModelProperty(value = "status")
    private String status;

    @Column(name = "`log`")
    @ApiModelProperty(value = "log")
    private String log;

    @Column(name = "`create_time`")
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    public void copy(WsNumberHistory source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
