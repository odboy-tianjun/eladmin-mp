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

import cn.odboy.model.PageResult;
import cn.odboy.modules.maint.domain.DeployHistory;
import cn.odboy.modules.maint.domain.dto.DeployHistoryQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author zhanghouying
 */
public interface DeployHistoryService extends IService<DeployHistory> {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<DeployHistory> queryAll(DeployHistoryQueryArgs criteria, Page<Object> page);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<DeployHistory> queryAll(DeployHistoryQueryArgs criteria);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(DeployHistory resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void delete(Set<String> ids);

    /**
     * 导出数据
     *
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void download(List<DeployHistory> queryAll, HttpServletResponse response) throws IOException;
}
