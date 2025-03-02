/*
 *  Copyright 2019-2025 Zheng Jie
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
package cn.odboy.modules.maint.rest;

import cn.odboy.system.logging.annotation.Log;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.maint.domain.Database;
import cn.odboy.system.maint.model.DatabaseQueryArgs;
import cn.odboy.system.maint.service.DatabaseService;
import cn.odboy.system.maint.util.SqlUtil;
import cn.odboy.util.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@Api(tags = "运维：数据库管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/database")
public class DatabaseController {
    private final String fileSavePath = FileUtil.getTmpDirPath() + "/";
    private final DatabaseService databaseService;

    @ApiOperation("导出数据库数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('database:list')")
    public void exportDatabase(HttpServletResponse response, DatabaseQueryArgs criteria) throws IOException {
        databaseService.download(databaseService.queryAll(criteria), response);
    }

    @ApiOperation(value = "查询数据库")
    @GetMapping
    @PreAuthorize("@el.check('database:list')")
    public ResponseEntity<PageResult<Database>> queryDatabase(DatabaseQueryArgs criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(databaseService.queryAll(criteria, page), HttpStatus.OK);
    }

    @Log("新增数据库")
    @ApiOperation(value = "新增数据库")
    @PostMapping
    @PreAuthorize("@el.check('database:add')")
    public ResponseEntity<Object> createDatabase(@Validated @RequestBody Database resources) {
        databaseService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改数据库")
    @ApiOperation(value = "修改数据库")
    @PutMapping
    @PreAuthorize("@el.check('database:edit')")
    public ResponseEntity<Object> updateDatabase(@Validated @RequestBody Database resources) {
        databaseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除数据库")
    @ApiOperation(value = "删除数据库")
    @DeleteMapping
    @PreAuthorize("@el.check('database:del')")
    public ResponseEntity<Object> deleteDatabase(@RequestBody Set<String> ids) {
        databaseService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("测试数据库链接")
    @ApiOperation(value = "测试数据库链接")
    @PostMapping("/testConnect")
    @PreAuthorize("@el.check('database:testConnect')")
    public ResponseEntity<Object> testConnect(@Validated @RequestBody Database resources) {
        return new ResponseEntity<>(databaseService.testConnection(resources), HttpStatus.CREATED);
    }

    @Log("执行SQL脚本")
    @ApiOperation(value = "执行SQL脚本")
    @PostMapping(value = "/upload")
    @PreAuthorize("@el.check('database:add')")
    public ResponseEntity<Object> uploadDatabase(@RequestBody MultipartFile file, HttpServletRequest request) throws Exception {
        String id = request.getParameter("id");
        Database database = databaseService.getById(id);
        String fileName;
        if (database != null) {
            fileName = FileUtil.verifyFilename(file.getOriginalFilename());
            File executeFile = new File(fileSavePath + fileName);
            FileUtil.del(executeFile);
            file.transferTo(executeFile);
            String result = SqlUtil.executeFile(database.getJdbcUrl(), database.getUserName(), database.getPwd(), executeFile);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            throw new BadRequestException("Database not exist");
        }
    }
}
