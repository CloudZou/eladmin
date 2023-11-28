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
import me.zhengjie.ws.domain.NumberTaskInfo;
import me.zhengjie.ws.service.NumberTaskInfoService;
import me.zhengjie.ws.service.dto.NumberTaskInfoQueryCriteria;
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
import me.zhengjie.ws.service.dto.NumberTaskInfoDto;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "号码任务管理管理")
@RequestMapping("/api/numberTaskInfo")
public class NumberTaskInfoController {

    private final NumberTaskInfoService numberTaskInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('numberTaskInfo:list')")
    public void exportNumberTaskInfo(HttpServletResponse response, NumberTaskInfoQueryCriteria criteria) throws IOException {
        numberTaskInfoService.download(numberTaskInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询号码任务管理")
    @ApiOperation("查询号码任务管理")
    @PreAuthorize("@el.check('numberTaskInfo:list')")
    public ResponseEntity<PageResult<NumberTaskInfoDto>> queryNumberTaskInfo(NumberTaskInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(numberTaskInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增号码任务管理")
    @ApiOperation("新增号码任务管理")
    @PreAuthorize("@el.check('numberTaskInfo:add')")
    public ResponseEntity<Object> createNumberTaskInfo(@Validated @RequestBody NumberTaskInfo resources){
        numberTaskInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改号码任务管理")
    @ApiOperation("修改号码任务管理")
    @PreAuthorize("@el.check('numberTaskInfo:edit')")
    public ResponseEntity<Object> updateNumberTaskInfo(@Validated @RequestBody NumberTaskInfo resources){
        numberTaskInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除号码任务管理")
    @ApiOperation("删除号码任务管理")
    @PreAuthorize("@el.check('numberTaskInfo:del')")
    public ResponseEntity<Object> deleteNumberTaskInfo(@RequestBody Integer[] ids) {
        numberTaskInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}