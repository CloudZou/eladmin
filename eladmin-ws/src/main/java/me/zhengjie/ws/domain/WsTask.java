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
@Table(name="ws_task")
public class WsTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "`name`")
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(name = "`proxy_group_id`")
    @ApiModelProperty(value = "proxyGroupId")
    private Integer proxyGroupId;

    @Column(name = "`proxy_group_name`")
    @ApiModelProperty(value = "代理分组")
    private String proxyGroupName;

    @Column(name = "`country_code`")
    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @Column(name = "`country_range`")
    @ApiModelProperty(value = "国家范围")
    private String countryRange;

    @Column(name = "`country_list`")
    @ApiModelProperty(value = "指定国家")
    private String countryList;

    @Column(name = "`number_type`")
    @ApiModelProperty(value = "接码平台")
    private String numberType;

    @Column(name = "`number_extra`")
    @ApiModelProperty(value = "接码配置")
    private String numberExtra;

    @Column(name = "`number_count`")
    @ApiModelProperty(value = "numberCount")
    private Integer numberCount;

    @Column(name = "`code_type`")
    @ApiModelProperty(value = "接码平台")
    private String codeType;

    @Column(name = "`account_type`")
    @ApiModelProperty(value = "账号类型")
    private String accountType;

    @Column(name = "`thread_count`")
    @ApiModelProperty(value = "线程数量")
    private Integer threadCount;

    @Column(name = "`code_wait_time`")
    @ApiModelProperty(value = "验证码等待时长")
    private Integer codeWaitTime;

    @Column(name = "`create_time`")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @Column(name = "`f2a_option`")
    @ApiModelProperty(value = "是否关闭二次验证")
    private Boolean f2aOption;

    public void copy(WsTask source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
