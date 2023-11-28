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

import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.ws.domain.WsProxyInfo;
import me.zhengjie.ws.service.WsProxyInfoService;
import me.zhengjie.ws.service.dto.WsProxyInfoQueryCriteria;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.*;
import java.net.URLEncoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.PageResult;
import me.zhengjie.ws.service.dto.WsProxyInfoDto;
import org.springframework.web.multipart.MultipartFile;

/**
* @website https://eladmin.vip
* @author eladmin
* @date 2023-11-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "WS接口管理")
@RequestMapping("/api/wsProxyInfo")
public class WsProxyInfoController {

    private final WsProxyInfoService wsProxyInfoService;

    @Resource
    private ResourceLoader resourceLoader;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wsProxyInfo:list')")
    public void exportWsProxyInfo(HttpServletResponse response, WsProxyInfoQueryCriteria criteria) throws IOException {
        wsProxyInfoService.download(wsProxyInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询WS接口")
    @ApiOperation("查询WS接口")
    @PreAuthorize("@el.check('wsProxyInfo:list')")
    public ResponseEntity<PageResult<WsProxyInfoDto>> queryWsProxyInfo(WsProxyInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wsProxyInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping("/uploadProxy")
    @ApiOperation("上传代理模板文件")
    @PreAuthorize("@el.check('wsProxyInfo:add')")
    public ResponseEntity<Object> uploadWsProxyTxt(@RequestParam Integer groupId, @RequestParam("file") MultipartFile file) throws Exception{
        wsProxyInfoService.uploadApiTxt(groupId, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/downloadProxy")
    @AnonymousAccess
    public void download(String fileType, HttpServletResponse response) {
        try {
            // path是指想要下载的文件的路径
            String path = "template/代理上传模板.txt";
            String filename = "代理上传模板";
            if (fileType.equals("proxy")) {
                path = "template/代理上传模板.txt";
            }
            if (fileType.equals("number")) {
                path = "template/api号码上传模板.txt";
                filename = "api号码上传模板";
            }
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:"+path);
            // 将文件写入输入流
            InputStream fis = resource.getInputStream();
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
//            response.addHeader("Content-Length", "" + resource.getInputStream().);
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @PostMapping
    @Log("新增WS接口")
    @ApiOperation("新增WS接口")
    @PreAuthorize("@el.check('wsProxyInfo:add')")
    public ResponseEntity<Object> createWsProxyInfo(@Validated @RequestBody WsProxyInfo resources){
        wsProxyInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改WS接口")
    @ApiOperation("修改WS接口")
    @PreAuthorize("@el.check('wsProxyInfo:edit')")
    public ResponseEntity<Object> updateWsProxyInfo(@Validated @RequestBody WsProxyInfo resources){
        wsProxyInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除WS接口")
    @ApiOperation("删除WS接口")
    @PreAuthorize("@el.check('wsProxyInfo:del')")
    public ResponseEntity<Object> deleteWsProxyInfo(@RequestBody Integer[] ids) {
        wsProxyInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}