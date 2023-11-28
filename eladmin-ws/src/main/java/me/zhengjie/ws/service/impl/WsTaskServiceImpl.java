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

import me.zhengjie.ws.domain.WsTask;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.WsProxyGroupRepository;
import me.zhengjie.ws.repository.WsTaskRepository;
import me.zhengjie.ws.service.WsTaskService;
import me.zhengjie.ws.service.dto.WsTaskDto;
import me.zhengjie.ws.service.dto.WsTaskQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsTaskMapper;
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
public class WsTaskServiceImpl implements WsTaskService {

    private final WsTaskRepository wsTaskRepository;
    private final WsTaskMapper wsTaskMapper;
    private final WsProxyGroupRepository wsProxyGroupRepository;

    @Override
    public PageResult<WsTaskDto> queryAll(WsTaskQueryCriteria criteria, Pageable pageable){
        Page<WsTask> page = wsTaskRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsTaskMapper::toDto));
    }

    @Override
    public List<WsTaskDto> queryAll(WsTaskQueryCriteria criteria){
        return wsTaskMapper.toDto(wsTaskRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsTaskDto findById(Integer id) {
        WsTask wsTask = wsTaskRepository.findById(id).orElseGet(WsTask::new);
        ValidationUtil.isNull(wsTask.getId(),"WsTask","id",id);
        return wsTaskMapper.toDto(wsTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsTask resources) {
        if (resources.getProxyGroupId() != null) {
            resources.setProxyGroupName(wsProxyGroupRepository.getById(resources.getId()).getName());
        }
        wsTaskRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsTask resources) {
        WsTask wsTask = wsTaskRepository.findById(resources.getId()).orElseGet(WsTask::new);
        ValidationUtil.isNull( wsTask.getId(),"WsTask","id",resources.getId());
        wsTask.copy(resources);
        wsTaskRepository.save(wsTask);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsTaskRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsTaskDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsTaskDto wsTask : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", wsTask.getName());
            map.put(" proxyGroupId",  wsTask.getProxyGroupId());
            map.put("代理分组", wsTask.getProxyGroupName());
            map.put("国家代码", wsTask.getCountryCode());
            map.put("国家范围", wsTask.getCountryRange());
            map.put("接码平台", wsTask.getNumberType());
            map.put("接码配置", wsTask.getNumberExtra());
            map.put("接码平台", wsTask.getCodeType());
            map.put("账号类型", wsTask.getAccountType());
            map.put("线程数量", wsTask.getThreadCount());
            map.put("验证码等待时长", wsTask.getCodeWaitTime());
            map.put("创建时间", wsTask.getCreateTime());
            map.put(" updateTime",  wsTask.getUpdateTime());
            map.put("是否关闭二次验证", wsTask.getF2aOption());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}