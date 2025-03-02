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
package cn.odboy.system.tools.service;

import cn.odboy.system.tools.domain.QiniuContent;
import cn.odboy.system.tools.model.QiniuQueryCriteria;
import cn.odboy.mybatisplus.model.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2018-12-31
 */
public interface QiniuContentService extends IService<QiniuContent> {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QiniuContent> searchQiniuContents(QiniuQueryCriteria criteria, Page<Object> page);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<QiniuContent> listQiniuContents(QiniuQueryCriteria criteria);

    /**
     * 上传文件
     *
     * @param file        文件
     * @return QiniuContent
     */
    QiniuContent createQiniuContent(MultipartFile file);

    /**
     * 下载文件
     *
     * @param id 文件id
     * @return String
     */
    String getDownloadUrl(Long id);

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    void deleteQiniuContent(Long id);

    /**
     * 同步数据
     *
     */
    void synchronize();

    /**
     * 删除文件
     *
     * @param ids    文件ID数组
     */
    void deleteQiniuContents(Long[] ids);

    /**
     * 导出数据
     *
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException;
}
