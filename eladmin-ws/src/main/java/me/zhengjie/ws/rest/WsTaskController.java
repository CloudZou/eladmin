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
import me.zhengjie.ws.domain.WsTask;
import me.zhengjie.ws.service.WsTaskService;
import me.zhengjie.ws.service.dto.WsTaskQueryCriteria;
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
import me.zhengjie.ws.service.dto.WsTaskDto;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "任务管理管理")
@RequestMapping("/api/wsTask")
public class WsTaskController {

    private final WsTaskService wsTaskService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wsTask:list')")
    public void exportWsTask(HttpServletResponse response, WsTaskQueryCriteria criteria) throws IOException {
        wsTaskService.download(wsTaskService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询任务管理")
    @ApiOperation("查询任务管理")
    @PreAuthorize("@el.check('wsTask:list')")
    public ResponseEntity<PageResult<WsTaskDto>> queryWsTask(WsTaskQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wsTaskService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增任务管理")
    @ApiOperation("新增任务管理")
    @PreAuthorize("@el.check('wsTask:add')")
    public ResponseEntity<Object> createWsTask(@Validated @RequestBody WsTask resources){
        wsTaskService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改任务管理")
    @ApiOperation("修改任务管理")
    @PreAuthorize("@el.check('wsTask:edit')")
    public ResponseEntity<Object> updateWsTask(@Validated @RequestBody WsTask resources){
        wsTaskService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除任务管理")
    @ApiOperation("删除任务管理")
    @PreAuthorize("@el.check('wsTask:del')")
    public ResponseEntity<Object> deleteWsTask(@RequestBody Integer[] ids) {
        wsTaskService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}