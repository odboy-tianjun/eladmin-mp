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
package cn.odboy.system.maint.service;

import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.maint.domain.Server;
import cn.odboy.system.maint.model.ServerQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
public interface ServerService extends IService<Server> {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<Server> queryAll(ServerQueryArgs criteria, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param criteria 条件
     * @return /
     */
    List<Server> queryAll(ServerQueryArgs criteria);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(Server resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(Server resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 根据IP查询
     *
     * @param ip /
     * @return /
     */
    Server findByIp(String ip);

    /**
     * 测试连接
     *
     * @param resources /
     * @return /
     */
    Boolean testConnect(Server resources);

    /**
     * 导出数据
     *
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void download(List<Server> queryAll, HttpServletResponse response) throws IOException;
}
