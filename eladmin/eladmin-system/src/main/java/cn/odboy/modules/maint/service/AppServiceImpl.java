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
package cn.odboy.modules.maint.service;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.maint.domain.App;
import cn.odboy.system.maint.model.AppQueryArgs;
import cn.odboy.modules.maint.mapper.AppMapper;
import cn.odboy.modules.maint.mapper.DeployMapper;
import cn.odboy.modules.maint.mapper.DeployServerMapper;
import cn.odboy.system.maint.service.AppService;
import cn.odboy.util.FileUtil;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@Service
@RequiredArgsConstructor
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    private final AppMapper appMapper;
    private final DeployMapper deployMapper;
    private final DeployServerMapper deployServerMapper;

    @Override
    public PageResult<App> queryAll(AppQueryArgs criteria, Page<Object> page) {
        return PageUtil.toPage(appMapper.selectApps(criteria, page));
    }

    @Override
    public List<App> queryAll(AppQueryArgs criteria) {
        return appMapper.selectApps(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(App resources) {
        verification(resources);
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(App resources) {
        verification(resources);
        App app = getById(resources.getId());
        app.copy(resources);
        saveOrUpdate(app);
    }

    private void verification(App resources) {
        String opt = "/opt";
        String home = "/home";
        if (!(resources.getUploadPath().startsWith(opt) || resources.getUploadPath().startsWith(home))) {
            throw new BadRequestException("文件只能上传在opt目录或者home目录 ");
        }
        if (!(resources.getDeployPath().startsWith(opt) || resources.getDeployPath().startsWith(home))) {
            throw new BadRequestException("文件只能部署在opt目录或者home目录 ");
        }
        if (!(resources.getBackupPath().startsWith(opt) || resources.getBackupPath().startsWith(home))) {
            throw new BadRequestException("文件只能备份在opt目录或者home目录 ");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        // 删除应用
        removeBatchByIds(ids);
        // 删除部署
        Set<Long> deployIds = deployMapper.selectDeployIdsByAppIds(ids);
        if (CollUtil.isNotEmpty(deployIds)) {
            deployServerMapper.deleteByDeployIds(deployIds);
            deployMapper.deleteByIds(deployIds);
        }
    }

    @Override
    public void download(List<App> apps, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (App app : apps) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("应用名称", app.getName());
            map.put("端口", app.getPort());
            map.put("上传目录", app.getUploadPath());
            map.put("部署目录", app.getDeployPath());
            map.put("备份目录", app.getBackupPath());
            map.put("启动脚本", app.getStartScript());
            map.put("部署脚本", app.getDeployScript());
            map.put("创建日期", app.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
