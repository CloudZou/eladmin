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
import me.zhengjie.ws.domain.WsNumberHistory;
import me.zhengjie.ws.service.WsNumberHistoryService;
import me.zhengjie.ws.service.dto.WsNumberHistoryQueryCriteria;
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
import me.zhengjie.ws.service.dto.WsNumberHistoryDto;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "WS接口管理")
@RequestMapping("/api/wsNumberHistory")
public class WsNumberHistoryController {

    private final WsNumberHistoryService wsNumberHistoryService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wsNumberHistory:list')")
    public void exportWsNumberHistory(HttpServletResponse response, WsNumberHistoryQueryCriteria criteria) throws IOException {
        wsNumberHistoryService.download(wsNumberHistoryService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询WS接口")
    @ApiOperation("查询WS接口")
    @PreAuthorize("@el.check('wsNumberHistory:list')")
    public ResponseEntity<PageResult<WsNumberHistoryDto>> queryWsNumberHistory(WsNumberHistoryQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wsNumberHistoryService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增WS接口")
    @ApiOperation("新增WS接口")
    @PreAuthorize("@el.check('wsNumberHistory:add')")
    public ResponseEntity<Object> createWsNumberHistory(@Validated @RequestBody WsNumberHistory resources){
        wsNumberHistoryService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改WS接口")
    @ApiOperation("修改WS接口")
    @PreAuthorize("@el.check('wsNumberHistory:edit')")
    public ResponseEntity<Object> updateWsNumberHistory(@Validated @RequestBody WsNumberHistory resources){
        wsNumberHistoryService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除WS接口")
    @ApiOperation("删除WS接口")
    @PreAuthorize("@el.check('wsNumberHistory:del')")
    public ResponseEntity<Object> deleteWsNumberHistory(@RequestBody Integer[] ids) {
        wsNumberHistoryService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}