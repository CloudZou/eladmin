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
import me.zhengjie.ws.domain.WsProxyGroup;
import me.zhengjie.ws.service.WsProxyGroupService;
import me.zhengjie.ws.service.dto.WsProxyGroupQueryCriteria;
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
import me.zhengjie.ws.service.dto.WsProxyGroupDto;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "WS接口管理")
@RequestMapping("/api/wsProxyGroup")
public class WsProxyGroupController {

    private final WsProxyGroupService wsProxyGroupService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wsProxyGroup:list')")
    public void exportWsProxyGroup(HttpServletResponse response, WsProxyGroupQueryCriteria criteria) throws IOException {
        wsProxyGroupService.download(wsProxyGroupService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询WS接口")
    @ApiOperation("查询WS接口")
    @PreAuthorize("@el.check('wsProxyGroup:list')")
    public ResponseEntity<PageResult<WsProxyGroupDto>> queryWsProxyGroup(WsProxyGroupQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wsProxyGroupService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增WS接口")
    @ApiOperation("新增WS接口")
    @PreAuthorize("@el.check('wsProxyGroup:add')")
    public ResponseEntity<Object> createWsProxyGroup(@Validated @RequestBody WsProxyGroup resources){
        wsProxyGroupService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改WS接口")
    @ApiOperation("修改WS接口")
    @PreAuthorize("@el.check('wsProxyGroup:edit')")
    public ResponseEntity<Object> updateWsProxyGroup(@Validated @RequestBody WsProxyGroup resources){
        wsProxyGroupService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除WS接口")
    @ApiOperation("删除WS接口")
    @PreAuthorize("@el.check('wsProxyGroup:del')")
    public ResponseEntity<Object> deleteWsProxyGroup(@RequestBody Integer[] ids) {
        wsProxyGroupService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}