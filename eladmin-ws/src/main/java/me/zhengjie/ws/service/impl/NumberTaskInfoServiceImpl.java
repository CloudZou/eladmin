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

import me.zhengjie.ws.domain.NumberTaskInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.NumberTaskInfoRepository;
import me.zhengjie.ws.service.NumberTaskInfoService;
import me.zhengjie.ws.service.dto.NumberTaskInfoDto;
import me.zhengjie.ws.service.dto.NumberTaskInfoQueryCriteria;
import me.zhengjie.ws.service.mapstruct.NumberTaskInfoMapper;
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
* @date 2023-11-28
**/
@Service
@RequiredArgsConstructor
public class NumberTaskInfoServiceImpl implements NumberTaskInfoService {

    private final NumberTaskInfoRepository numberTaskInfoRepository;
    private final NumberTaskInfoMapper numberTaskInfoMapper;

    @Override
    public PageResult<NumberTaskInfoDto> queryAll(NumberTaskInfoQueryCriteria criteria, Pageable pageable){
        Page<NumberTaskInfo> page = numberTaskInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(numberTaskInfoMapper::toDto));
    }

    @Override
    public List<NumberTaskInfoDto> queryAll(NumberTaskInfoQueryCriteria criteria){
        return numberTaskInfoMapper.toDto(numberTaskInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public NumberTaskInfoDto findById(Integer id) {
        NumberTaskInfo numberTaskInfo = numberTaskInfoRepository.findById(id).orElseGet(NumberTaskInfo::new);
        ValidationUtil.isNull(numberTaskInfo.getId(),"NumberTaskInfo","id",id);
        return numberTaskInfoMapper.toDto(numberTaskInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NumberTaskInfo resources) {
        numberTaskInfoRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void initNumberTaskInfo(NumberTaskInfo resources) {
        NumberTaskInfoQueryCriteria criteria = new NumberTaskInfoQueryCriteria();
        criteria.setPhoneNum(resources.getPhoneNum());
        List<NumberTaskInfo> numberTaskInfos = numberTaskInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        NumberTaskInfo existNumberTaskInfo = null;
        if (numberTaskInfos != null && numberTaskInfos.size() > 0) {
            existNumberTaskInfo = numberTaskInfos.get(0);
            existNumberTaskInfo.setReasonStr(resources.getReasonStr());
            existNumberTaskInfo.setProtocols(resources.getProtocols());
            existNumberTaskInfo.setTaskName(resources.getTaskName());
            existNumberTaskInfo.setStartTime(resources.getStartTime());
            existNumberTaskInfo.setSmsUrlStr(resources.getSmsUrlStr());
            existNumberTaskInfo.setWaitTime(resources.getWaitTime());
            existNumberTaskInfo.setRunTimestamp(resources.getRunTimestamp());
            existNumberTaskInfo.setNumberType(resources.getNumberType());

            this.update(existNumberTaskInfo);

        } else {
            numberTaskInfoRepository.save(resources);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(NumberTaskInfo resources) {
        NumberTaskInfo numberTaskInfo = numberTaskInfoRepository.findById(resources.getId()).orElseGet(NumberTaskInfo::new);
        ValidationUtil.isNull( numberTaskInfo.getId(),"NumberTaskInfo","id",resources.getId());
        numberTaskInfo.copy(resources);
        numberTaskInfoRepository.save(numberTaskInfo);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            numberTaskInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<NumberTaskInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (NumberTaskInfoDto numberTaskInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" taskName",  numberTaskInfo.getTaskName());
            map.put(" phoneNum",  numberTaskInfo.getPhoneNum());
            map.put(" smsUrlStr",  numberTaskInfo.getSmsUrlStr());
            map.put(" reasonStr",  numberTaskInfo.getReasonStr());
            map.put(" waitTime",  numberTaskInfo.getWaitTime());
            map.put(" runTimestamp",  numberTaskInfo.getRunTimestamp());
            map.put(" protocols",  numberTaskInfo.getProtocols());
            map.put(" numberType",  numberTaskInfo.getNumberType());
            map.put(" startTime",  numberTaskInfo.getStartTime());
            map.put(" endTime",  numberTaskInfo.getEndTime());
            map.put(" updateTime",  numberTaskInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}