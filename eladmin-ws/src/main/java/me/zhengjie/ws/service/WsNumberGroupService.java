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
package me.zhengjie.ws.service;

import me.zhengjie.ws.domain.WsNumberGroup;
import me.zhengjie.ws.service.dto.WsNumberGroupDto;
import me.zhengjie.ws.service.dto.WsNumberGroupQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.PageResult;

/**
* @website https://eladmin.vip
* @description 服务接口
* @author eladmin
* @date 2023-11-25
**/
public interface WsNumberGroupService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<WsNumberGroupDto> queryAll(WsNumberGroupQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<WsNumberGroupDto>
    */
    List<WsNumberGroupDto> queryAll(WsNumberGroupQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return WsNumberGroupDto
     */
    WsNumberGroupDto findById(Integer id);

    /**
    * 创建
    * @param resources /
    */
    void create(WsNumberGroup resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(WsNumberGroup resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Integer[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<WsNumberGroupDto> all, HttpServletResponse response) throws IOException;
}