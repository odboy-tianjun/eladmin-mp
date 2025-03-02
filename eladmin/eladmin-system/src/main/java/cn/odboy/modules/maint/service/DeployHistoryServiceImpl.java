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

import cn.hutool.core.util.IdUtil;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.maint.domain.DeployHistory;
import cn.odboy.system.maint.model.DeployHistoryQueryArgs;
import cn.odboy.modules.maint.mapper.DeployHistoryMapper;
import cn.odboy.system.maint.service.DeployHistoryService;
import cn.odboy.util.DateUtil;
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
public class DeployHistoryServiceImpl extends ServiceImpl<DeployHistoryMapper, DeployHistory> implements DeployHistoryService {
    private final DeployHistoryMapper deployhistoryMapper;

    @Override
    public PageResult<DeployHistory> queryAll(DeployHistoryQueryArgs criteria, Page<Object> page) {
        return PageUtil.toPage(deployhistoryMapper.selectDeployHistory(criteria, page));
    }

    @Override
    public List<DeployHistory> queryAll(DeployHistoryQueryArgs criteria) {
        return deployhistoryMapper.selectDeployHistory(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeployHistory resources) {
        resources.setId(IdUtil.simpleUUID());
        resources.setDeployDate(DateUtil.getTimeStamp());
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<DeployHistory> deployHistories, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeployHistory deployHistory : deployHistories) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部署编号", deployHistory.getDeployId());
            map.put("应用名称", deployHistory.getAppName());
            map.put("部署IP", deployHistory.getIp());
            map.put("部署时间", deployHistory.getDeployDate());
            map.put("部署人员", deployHistory.getDeployUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
