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
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.maint.domain.DeployHistory;
import cn.odboy.system.maint.model.DeployHistoryQueryArgs;
import cn.odboy.system.maint.service.DeployHistoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "运维：部署历史管理")
@RequestMapping("/api/deployHistory")
public class DeployHistoryController {
    private final DeployHistoryService deployhistoryService;

    @ApiOperation("导出部署历史数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deployHistory:list')")
    public void exportDeployHistory(HttpServletResponse response, DeployHistoryQueryArgs criteria) throws IOException {
        deployhistoryService.download(deployhistoryService.queryAll(criteria), response);
    }

    @ApiOperation(value = "查询部署历史")
    @GetMapping
    @PreAuthorize("@el.check('deployHistory:list')")
    public ResponseEntity<PageResult<DeployHistory>> queryDeployHistory(DeployHistoryQueryArgs criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(deployhistoryService.queryAll(criteria, page), HttpStatus.OK);
    }

    @Log("删除DeployHistory")
    @ApiOperation(value = "删除部署历史")
    @DeleteMapping
    @PreAuthorize("@el.check('deployHistory:del')")
    public ResponseEntity<Object> deleteDeployHistory(@RequestBody Set<String> ids) {
        deployhistoryService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
