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
package cn.odboy.rest;

import cn.odboy.annotation.Log;
import cn.odboy.domain.QiniuConfig;
import cn.odboy.domain.QiniuContent;
import cn.odboy.domain.vo.QiniuQueryCriteria;
import cn.odboy.model.PageResult;
import cn.odboy.service.QiNiuConfigService;
import cn.odboy.service.QiniuContentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送邮件
 *
 * @author 郑杰
 * @date 2018/09/28 6:55:53
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qiNiuContent")
@Api(tags = "工具：七牛云存储管理")
public class QiniuController {
    private final QiniuContentService qiniuContentService;
    private final QiNiuConfigService qiNiuConfigService;

    @GetMapping(value = "/config")
    public ResponseEntity<QiniuConfig> describeQiniuConfig() {
        return new ResponseEntity<>(qiNiuConfigService.describeQiniuConfig(), HttpStatus.OK);
    }

    @Log("配置七牛云存储")
    @ApiOperation("配置七牛云存储")
    @PutMapping(value = "/config")
    public ResponseEntity<Object> modifyQiniuConfigType(@Validated @RequestBody QiniuConfig qiniuConfig) {
        qiNiuConfigService.saveQiniuConfig(qiniuConfig);
        qiNiuConfigService.modifyQiniuConfigType(qiniuConfig.getType());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void listQiniuContents(HttpServletResponse response, QiniuQueryCriteria criteria) throws IOException {
        qiniuContentService.downloadList(qiniuContentService.listQiniuContents(criteria), response);
    }

    @ApiOperation("查询文件")
    @GetMapping
    public ResponseEntity<PageResult<QiniuContent>> searchQiniuContents(QiniuQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(qiniuContentService.searchQiniuContents(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping
    public ResponseEntity<Object> createQiniuContent(@RequestParam MultipartFile file) {
        QiniuContent qiniuContent = qiniuContentService.createQiniuContent(file);
        Map<String, Object> map = new HashMap<>();
        map.put("id", qiniuContent.getId());
        map.put("errno", 0);
        map.put("data", new String[]{qiniuContent.getUrl()});
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Log("同步七牛云数据")
    @ApiOperation("同步七牛云数据")
    @PostMapping(value = "/synchronize")
    public ResponseEntity<Object> synchronize() {
        qiniuContentService.synchronize();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("下载文件")
    @ApiOperation("下载文件")
    @GetMapping(value = "/download/{id}")
    public ResponseEntity<Object> getDownloadUrl(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", qiniuContentService.getDownloadUrl(id));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Log("删除文件")
    @ApiOperation("删除文件")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteQiniuContent(@PathVariable Long id) {
        qiniuContentService.deleteQiniuContent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除多张图片")
    @ApiOperation("删除多张图片")
    @DeleteMapping
    public ResponseEntity<Object> deleteQiniuContents(@RequestBody Long[] ids) {
        qiniuContentService.deleteQiniuContents(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
