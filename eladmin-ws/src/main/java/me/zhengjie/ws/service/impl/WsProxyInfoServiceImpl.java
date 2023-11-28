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

import cn.hutool.core.util.ObjectUtil;
import com.sun.java.browser.net.ProxyInfo;
import me.zhengjie.config.FileProperties;
import me.zhengjie.domain.LocalStorage;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.*;
import me.zhengjie.ws.domain.WsProxyGroup;
import me.zhengjie.ws.domain.WsProxyInfo;
import lombok.RequiredArgsConstructor;
import me.zhengjie.ws.repository.WsProxyGroupRepository;
import me.zhengjie.ws.repository.WsProxyInfoRepository;
import me.zhengjie.ws.service.WsProxyInfoService;
import me.zhengjie.ws.service.dto.WsProxyInfoDto;
import me.zhengjie.ws.service.dto.WsProxyInfoQueryCriteria;
import me.zhengjie.ws.service.mapstruct.WsProxyGroupMapper;
import me.zhengjie.ws.service.mapstruct.WsProxyInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.*;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.web.multipart.MultipartFile;

/**
* @website https://eladmin.vip
* @description 服务实现
* @author eladmin
* @date 2023-11-25
**/
@Service
@RequiredArgsConstructor
public class WsProxyInfoServiceImpl implements WsProxyInfoService {

    private final WsProxyInfoRepository wsProxyInfoRepository;
    private final WsProxyInfoMapper wsProxyInfoMapper;
    private final WsProxyGroupRepository wsProxyGroupRepository;
    private final WsProxyGroupMapper wsProxyGroupMapper;
    private final FileProperties properties;

    @Override
    public PageResult<WsProxyInfoDto> queryAll(WsProxyInfoQueryCriteria criteria, Pageable pageable){
        Page<WsProxyInfo> page = wsProxyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wsProxyInfoMapper::toDto));
    }

    @Override
    public List<WsProxyInfoDto> queryAll(WsProxyInfoQueryCriteria criteria){
        return wsProxyInfoMapper.toDto(wsProxyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WsProxyInfoDto findById(Integer id) {
        WsProxyInfo wsProxyInfo = wsProxyInfoRepository.findById(id).orElseGet(WsProxyInfo::new);
        ValidationUtil.isNull(wsProxyInfo.getId(),"WsProxyInfo","id",id);
        return wsProxyInfoMapper.toDto(wsProxyInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(WsProxyInfo resources) {
        if (resources.getGroupId() != null) {
            resources.setGroupName(wsProxyGroupRepository.getById(resources.getGroupId()).getName());
        }
        wsProxyInfoRepository.save(resources);
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
        List<WsProxyInfo> wsProxyInfos = new ArrayList<>();
        WsProxyGroup proxyGroup = wsProxyGroupRepository.getById(groupId);
        while((str = bufferedReader.readLine()) != null)
        {
            for (int i=0; i< splitStrings.length; i++) {
                if (str.contains(splitStrings[i])) {
                    String[] proxyInfos = str.split(splitStrings[i]);
                    String proxyUsername = proxyInfos[0];
                    String proxyPassword = proxyInfos[1];
                    String proxyAddress = proxyInfos[2];
                    String proxyPort = proxyInfos[3];
                    WsProxyInfo wsProxyInfo = new WsProxyInfo();
                    wsProxyInfo.setGroupId(groupId);
                    wsProxyInfo.setGroupName(proxyGroup.getName());
                    wsProxyInfo.setProxyUsername(proxyUsername);
                    wsProxyInfo.setProxyPassword(proxyPassword);
                    wsProxyInfo.setProxyAddress(proxyAddress);
                    wsProxyInfo.setProxyPort(proxyPort);
                    wsProxyInfos.add(wsProxyInfo);
                    break;
                }
            }
        }
        wsProxyInfoRepository.saveAll(wsProxyInfos);
        bufferedReader.close();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WsProxyInfo resources) {
        WsProxyInfo wsProxyInfo = wsProxyInfoRepository.findById(resources.getId()).orElseGet(WsProxyInfo::new);
        ValidationUtil.isNull( wsProxyInfo.getId(),"WsProxyInfo","id",resources.getId());
        wsProxyInfo.copy(resources);
        wsProxyInfoRepository.save(wsProxyInfo);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wsProxyInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WsProxyInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WsProxyInfoDto wsProxyInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" groupId",  wsProxyInfo.getGroupId());
            map.put("分组名称", wsProxyInfo.getGroupName());
            map.put("代理账号", wsProxyInfo.getProxyUsername());
            map.put("代理地址", wsProxyInfo.getProxyAddress());
            map.put("代理端口", wsProxyInfo.getProxyPort());
            map.put("代理密码", wsProxyInfo.getProxyPassword());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}