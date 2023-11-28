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

import me.zhengjie.ws.domain.WsNumberHistory;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.WsNumberHistoryRepository;
import me.zhengjie.ws.service.WsNumberHistoryService;
import me.zhengjie.ws.service.dto.WsNumberHistoryDto;
import me.zhengjie.ws.service.dto.WsNumberHistoryQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsNumberHistoryMapper;
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
public class WsNumberHistoryServiceImpl implements WsNumberHistoryService {

    private final WsNumberHistoryRepository wsNumberHistoryRepository;
    private final WsNumberHistoryMapper wsNumberHistoryMapper;

    @Override
    public PageResult<WsNumberHistoryDto> queryAll(WsNumberHistoryQueryCriteria criteria, Pageable pageable){
        Page<WsNumberHistory> page = wsNumberHistoryRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsNumberHistoryMapper::toDto));
    }

    @Override
    public List<WsNumberHistoryDto> queryAll(WsNumberHistoryQueryCriteria criteria){
        return wsNumberHistoryMapper.toDto(wsNumberHistoryRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsNumberHistoryDto findById(Integer id) {
        WsNumberHistory wsNumberHistory = wsNumberHistoryRepository.findById(id).orElseGet(WsNumberHistory::new);
        ValidationUtil.isNull(wsNumberHistory.getId(),"WsNumberHistory","id",id);
        return wsNumberHistoryMapper.toDto(wsNumberHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsNumberHistory resources) {
        wsNumberHistoryRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsNumberHistory resources) {
        WsNumberHistory wsNumberHistory = wsNumberHistoryRepository.findById(resources.getId()).orElseGet(WsNumberHistory::new);
        ValidationUtil.isNull( wsNumberHistory.getId(),"WsNumberHistory","id",resources.getId());
        wsNumberHistory.copy(resources);
        wsNumberHistoryRepository.save(wsNumberHistory);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsNumberHistoryRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsNumberHistoryDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsNumberHistoryDto wsNumberHistory : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" groupId",  wsNumberHistory.getGroupId());
            map.put(" groupName",  wsNumberHistory.getGroupName());
            map.put(" number",  wsNumberHistory.getNumber());
            map.put(" startTime",  wsNumberHistory.getStartTime());
            map.put(" endTime",  wsNumberHistory.getEndTime());
            map.put(" status",  wsNumberHistory.getStatus());
            map.put(" log",  wsNumberHistory.getLog());
            map.put(" createTime",  wsNumberHistory.getCreateTime());
            map.put(" updateTime",  wsNumberHistory.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}