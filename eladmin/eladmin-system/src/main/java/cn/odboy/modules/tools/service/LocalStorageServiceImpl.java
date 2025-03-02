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
package cn.odboy.modules.tools.service;

import cn.hutool.core.util.ObjectUtil;
import cn.odboy.system.tools.domain.LocalStorage;
import cn.odboy.system.tools.model.LocalStorageQueryCriteria;
import cn.odboy.exception.BadRequestException;
import cn.odboy.upload.FileProperties;
import cn.odboy.modules.tools.mapper.LocalStorageMapper;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.tools.service.LocalStorageService;
import cn.odboy.util.FileUtil;
import cn.odboy.util.PageUtil;
import cn.odboy.util.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2019-09-05
 */
@Service
@RequiredArgsConstructor
public class LocalStorageServiceImpl extends ServiceImpl<LocalStorageMapper, LocalStorage> implements LocalStorageService {
    private final LocalStorageMapper localStorageMapper;
    private final FileProperties properties;

    @Override
    public PageResult<LocalStorage> searchLocalStorages(LocalStorageQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(localStorageMapper.selectLocalStorages(criteria, page));
    }

    @Override
    public List<LocalStorage> listLocalStorages(LocalStorageQueryCriteria criteria) {
        return localStorageMapper.selectLocalStorages(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LocalStorage createLocalStorage(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        String size = FileUtil.getSize(multipartFile.getSize());
        String filename = StringUtil.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        try {
            LocalStorage localStorage = new LocalStorage(
                    file.getName(),
                    filename,
                    suffix,
                    file.getPath(),
                    type,
                    size
            );
            save(localStorage);
            return localStorage;
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLocalStorage(LocalStorage resources) {
        LocalStorage localStorage = getById(resources.getId());
        localStorage.copy(resources);
        saveOrUpdate(localStorage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLocalStorages(Long[] ids) {
        for (Long id : ids) {
            LocalStorage storage = getById(id);
            FileUtil.del(storage.getPath());
            removeById(storage);
        }
    }

    @Override
    public void download(List<LocalStorage> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocalStorage localStorage : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", localStorage.getRealName());
            map.put("备注名", localStorage.getName());
            map.put("文件类型", localStorage.getType());
            map.put("文件大小", localStorage.getSize());
            map.put("创建者", localStorage.getCreateBy());
            map.put("创建日期", localStorage.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
