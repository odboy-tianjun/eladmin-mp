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
package cn.odboy.modules.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.context.CacheKey;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.core.domain.Dict;
import cn.odboy.system.core.domain.DictDetail;
import cn.odboy.system.core.model.DictQueryArgs;
import cn.odboy.modules.system.mapper.DictDetailMapper;
import cn.odboy.modules.system.mapper.DictMapper;
import cn.odboy.system.core.service.DictService;
import cn.odboy.util.FileUtil;
import cn.odboy.util.PageUtil;
import cn.odboy.util.RedisUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Zheng Jie
 * @date 2019-04-10
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    private final DictMapper dictMapper;
    private final RedisUtil redisUtil;
    private final DictDetailMapper deleteDetail;

    @Override
    public PageResult<Dict> queryAll(DictQueryArgs criteria, Page<Object> page) {
        criteria.setOffset(page.offset());
        List<Dict> dicts = dictMapper.selectDicts(criteria);
        Long total = dictMapper.countByBlurry(criteria);
        return PageUtil.toPage(dicts, total);
    }

    @Override
    public List<Dict> queryAll(DictQueryArgs criteria) {
        return dictMapper.selectDicts(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Dict resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Dict resources) {
        // 清理缓存
        delCaches(resources);
        Dict dict = getById(resources.getId());
        dict.setName(resources.getName());
        dict.setDescription(resources.getDescription());
        saveOrUpdate(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        // 清理缓存
        List<Dict> dicts = dictMapper.selectByIds(ids);
        for (Dict dict : dicts) {
            delCaches(dict);
        }
        // 删除字典
        dictMapper.deleteByIds(ids);
        // 删除字典详情
        deleteDetail.deleteByDictIds(ids);
    }

    @Override
    public void download(List<Dict> dicts, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Dict dict : dicts) {
            if (CollectionUtil.isNotEmpty(dict.getDictDetails())) {
                for (DictDetail dictDetail : dict.getDictDetails()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dict.getName());
                    map.put("字典描述", dict.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", dict.getName());
                map.put("字典描述", dict.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dict.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public void delCaches(Dict dict) {
        redisUtil.del(CacheKey.DICT_NAME + dict.getName());
    }
}