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

import me.zhengjie.ws.domain.WsProxyGroup;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.WsProxyGroupRepository;
import me.zhengjie.ws.service.WsProxyGroupService;
import me.zhengjie.ws.service.dto.WsProxyGroupDto;
import me.zhengjie.ws.service.dto.WsProxyGroupQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsProxyGroupMapper;
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
public class WsProxyGroupServiceImpl implements WsProxyGroupService {

    private final WsProxyGroupRepository wsProxyGroupRepository;
    private final WsProxyGroupMapper wsProxyGroupMapper;

    @Override
    public PageResult<WsProxyGroupDto> queryAll(WsProxyGroupQueryCriteria criteria, Pageable pageable){
        Page<WsProxyGroup> page = wsProxyGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsProxyGroupMapper::toDto));
    }

    @Override
    public List<WsProxyGroupDto> queryAll(WsProxyGroupQueryCriteria criteria){
        return wsProxyGroupMapper.toDto(wsProxyGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsProxyGroupDto findById(Integer id) {
        WsProxyGroup wsProxyGroup = wsProxyGroupRepository.findById(id).orElseGet(WsProxyGroup::new);
        ValidationUtil.isNull(wsProxyGroup.getId(),"WsProxyGroup","id",id);
        return wsProxyGroupMapper.toDto(wsProxyGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsProxyGroup resources) {
        wsProxyGroupRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsProxyGroup resources) {
        WsProxyGroup wsProxyGroup = wsProxyGroupRepository.findById(resources.getId()).orElseGet(WsProxyGroup::new);
        ValidationUtil.isNull( wsProxyGroup.getId(),"WsProxyGroup","id",resources.getId());
        wsProxyGroup.copy(resources);
        wsProxyGroupRepository.save(wsProxyGroup);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsProxyGroupRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsProxyGroupDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsProxyGroupDto wsProxyGroup : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", wsProxyGroup.getName());
            map.put("代理类型", wsProxyGroup.getType());
            map.put("代理平台", wsProxyGroup.getPlatform());
            map.put(" createTime",  wsProxyGroup.getCreateTime());
            map.put(" updateTime",  wsProxyGroup.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}