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
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author eladmin
* @date 2023-11-25
**/
@Entity
@Data
@Table(name="ws_proxy_info")
public class WsProxyInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "`group_id`")
    @ApiModelProperty(value = "groupId")
    private Integer groupId;

    @Column(name = "`group_name`")
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @Column(name = "`proxy_username`")
    @ApiModelProperty(value = "代理账号")
    private String proxyUsername;

    @Column(name = "`proxy_address`")
    @ApiModelProperty(value = "代理地址")
    private String proxyAddress;

    @Column(name = "`proxy_port`")
    @ApiModelProperty(value = "代理端口")
    private String proxyPort;

    @Column(name = "`proxy_password`")
    @ApiModelProperty(value = "代理密码")
    private String proxyPassword;

    public void copy(WsProxyInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
