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
package me.zhengjie.ws.service.impl;

import me.zhengjie.ws.domain.WsNumberGroup;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.WsNumberGroupRepository;
import me.zhengjie.ws.service.WsNumberGroupService;
import me.zhengjie.ws.service.dto.WsNumberGroupDto;
import me.zhengjie.ws.service.dto.WsNumberGroupQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsNumberGroupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import me.zhengjie.utils.PageResult;

/**
* @website https://eladmin.vip
* @description 服务实现
* @author eladmin
* @date 2023-11-25
**/
@Service
@RequiredArgsConstructor
public class WsNumberGroupServiceImpl implements WsNumberGroupService {

    private final WsNumberGroupRepository wsNumberGroupRepository;
    private final WsNumberGroupMapper wsNumberGroupMapper;

    @Override
    public PageResult<WsNumberGroupDto> queryAll(WsNumberGroupQueryCriteria criteria, Pageable pageable){
        Page<WsNumberGroup> page = wsNumberGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsNumberGroupMapper::toDto));
    }

    @Override
    public List<WsNumberGroupDto> queryAll(WsNumberGroupQueryCriteria criteria){
        return wsNumberGroupMapper.toDto(wsNumberGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsNumberGroupDto findById(Integer id) {
        WsNumberGroup wsNumberGroup = wsNumberGroupRepository.findById(id).orElseGet(WsNumberGroup::new);
        ValidationUtil.isNull(wsNumberGroup.getId(),"WsNumberGroup","id",id);
        return wsNumberGroupMapper.toDto(wsNumberGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsNumberGroup resources) {
        wsNumberGroupRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsNumberGroup resources) {
        WsNumberGroup wsNumberGroup = wsNumberGroupRepository.findById(resources.getId()).orElseGet(WsNumberGroup::new);
        ValidationUtil.isNull( wsNumberGroup.getId(),"WsNumberGroup","id",resources.getId());
        wsNumberGroup.copy(resources);
        wsNumberGroupRepository.save(wsNumberGroup);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsNumberGroupRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsNumberGroupDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsNumberGroupDto wsNumberGroup : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", wsNumberGroup.getName());
            map.put(" createTime",  wsNumberGroup.getCreateTime());
            map.put(" updateTime",  wsNumberGroup.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}