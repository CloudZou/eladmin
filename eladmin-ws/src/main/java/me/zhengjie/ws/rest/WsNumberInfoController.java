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
package me.zhengjie.ws.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.ws.domain.WsNumberInfo;
import me.zhengjie.ws.service.WsNumberInfoService;
import me.zhengjie.ws.service.dto.WsNumberInfoQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.PageResult;
import me.zhengjie.ws.service.dto.WsNumberInfoDto;
import org.springframework.web.multipart.MultipartFile;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "WS接口管理")
@RequestMapping("/api/wsNumberInfo")
public class WsNumberInfoController {

    private final WsNumberInfoService wsNumberInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wsNumberInfo:list')")
    public void exportWsNumberInfo(HttpServletResponse response, WsNumberInfoQueryCriteria criteria) throws IOException {
        wsNumberInfoService.download(wsNumberInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询WS接口")
    @ApiOperation("查询WS接口")
    @PreAuthorize("@el.check('wsNumberInfo:list')")
    public ResponseEntity<PageResult<WsNumberInfoDto>> queryWsNumberInfo(WsNumberInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wsNumberInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增WS接口")
    @ApiOperation("新增WS接口")
    @PreAuthorize("@el.check('wsNumberInfo:add')")
    public ResponseEntity<Object> createWsNumberInfo(@Validated @RequestBody WsNumberInfo resources){
        wsNumberInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/uploadNumber")
    @ApiOperation("上传代理模板文件")
    @PreAuthorize("@el.check('wsProxyInfo:add')")
    public ResponseEntity<Object> uploadNumberTxt(@RequestParam Integer groupId, @RequestParam("file") MultipartFile file) throws Exception{
        wsNumberInfoService.uploadApiTxt(groupId,file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改WS接口")
    @ApiOperation("修改WS接口")
    @PreAuthorize("@el.check('wsNumberInfo:edit')")
    public ResponseEntity<Object> updateWsNumberInfo(@Validated @RequestBody WsNumberInfo resources){
        wsNumberInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除WS接口")
    @ApiOperation("删除WS接口")
    @PreAuthorize("@el.check('wsNumberInfo:del')")
    public ResponseEntity<Object> deleteWsNumberInfo(@RequestBody Integer[] ids) {
        wsNumberInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}