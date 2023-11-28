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

import me.zhengjie.config.FileProperties;
import me.zhengjie.ws.domain.WsNumberGroup;
import me.zhengjie.ws.domain.WsNumberInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.domain.WsProxyInfo;
import me.zhengjie.ws.repository.WsNumberGroupRepository;
import me.zhengjie.ws.repository.WsNumberInfoRepository;
import me.zhengjie.ws.service.WsNumberInfoService;
import me.zhengjie.ws.service.dto.WsNumberInfoDto;
import me.zhengjie.ws.service.dto.WsNumberInfoQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsNumberInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import me.zhengjie.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
* @website https://eladmin.vip
* @description 服务实现
* @author eladmin
* @date 2023-11-25
**/
@Service
@RequiredArgsConstructor
public class WsNumberInfoServiceImpl implements WsNumberInfoService {

    private final WsNumberInfoRepository wsNumberInfoRepository;
    private final WsNumberInfoMapper wsNumberInfoMapper;
    private final WsNumberGroupRepository wsNumberGroupRepository;
    private final FileProperties properties;

    @Override
    public PageResult<WsNumberInfoDto> queryAll(WsNumberInfoQueryCriteria criteria, Pageable pageable){
        Page<WsNumberInfo> page = wsNumberInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsNumberInfoMapper::toDto));
    }

    @Override
    public List<WsNumberInfoDto> queryAll(WsNumberInfoQueryCriteria criteria){
        return wsNumberInfoMapper.toDto(wsNumberInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsNumberInfoDto findById(Integer id) {
        WsNumberInfo wsNumberInfo = wsNumberInfoRepository.findById(id).orElseGet(WsNumberInfo::new);
        ValidationUtil.isNull(wsNumberInfo.getId(),"WsNumberInfo","id",id);
        return wsNumberInfoMapper.toDto(wsNumberInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsNumberInfo resources) {
        wsNumberInfoRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsNumberInfo resources) {
        WsNumberInfo wsNumberInfo = wsNumberInfoRepository.findById(resources.getId()).orElseGet(WsNumberInfo::new);
        ValidationUtil.isNull( wsNumberInfo.getId(),"WsNumberInfo","id",resources.getId());
        wsNumberInfo.copy(resources);
        wsNumberInfoRepository.save(wsNumberInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void uploadApiTxt(Integer groupId, MultipartFile multipartFile) throws Exception{
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.toFile(multipartFile);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String str = null;
        String[] splitStrings = new String[]{"----",",","|"};
        WsNumberGroup numberGroup = wsNumberGroupRepository.getById(groupId);
        List<WsNumberInfo> wsNumberInfos = new ArrayList<>();
        while((str = bufferedReader.readLine()) != null)
        {
            for (int i=0; i< splitStrings.length; i++) {
                if (str.contains(splitStrings[i])) {
                    String[] proxyInfos = str.split(splitStrings[i]);
                    String number = proxyInfos[0];
                    String numberLink = proxyInfos[1];

                    WsNumberInfo wsNumberInfo = new WsNumberInfo();
                    wsNumberInfo.setGroupId(groupId);
                    wsNumberInfo.setGroupName(numberGroup.getName());
                    wsNumberInfo.setNumber(number);
                    wsNumberInfo.setNumberLink(numberLink);

                    wsNumberInfos.add(wsNumberInfo);
                    break;
                }
            }
        }
        wsNumberInfoRepository.saveAll(wsNumberInfos);
        bufferedReader.close();
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsNumberInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsNumberInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsNumberInfoDto wsNumberInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("号码", wsNumberInfo.getNumber());
            map.put(" groupId",  wsNumberInfo.getGroupId());
            map.put("号码分组", wsNumberInfo.getGroupName());
            map.put(" createTime",  wsNumberInfo.getCreateTime());
            map.put(" updateTime",  wsNumberInfo.getUpdateTime());
            map.put(" numberLink",  wsNumberInfo.getNumberLink());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}