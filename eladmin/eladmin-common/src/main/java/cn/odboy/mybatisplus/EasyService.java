/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.mybatisplus;

import cn.odboy.mybatisplus.model.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 公共抽象Mapper接口类
 *
 * @author odboy
 * @date 2022-04-03
 */
public interface EasyService<T> extends IService<T> {
    LambdaQueryWrapper<T> quick();

    <G> int quickSave(G resources);

    <G> int quickSaveBatch(List<G> resources);

    <G> int quickModifyById(G resources);

    <G> boolean quickModifyBatchById(Collection<G> resources, int batchSize);

    <G> G quickGetOneById(Serializable id, Class<G> targetClazz);

    T quickGetOne(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orderColumn);

    <G> G quickGetOne(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orderColumn, Class<G> clazz);

    <G> List<G> quickListByIds(List<Serializable> ids, Class<G> targetClazz);

    <Q> List<T> quickList(Q criteria);

    List<T> quickList(LambdaQueryWrapper<T> wrapper);

    <G, Q> List<G> quickList(Q criteria, Class<G> targetClazz);

    <G> List<G> quickList(LambdaQueryWrapper<T> wrapper, Class<G> targetClazz);

    PageResult<T> quickPage(LambdaQueryWrapper<T> wrapper, IPage<T> pageable);

    <G> PageResult<G> quickPage(LambdaQueryWrapper<T> wrapper, IPage<T> pageable, Class<G> targetClazz);

    <G, Q> PageResult<G> quickPage(Q criteria, IPage<T> pageable, Class<G> targetClazz);

    <Q> PageResult<T> quickPage(Q criteria, IPage<T> pageable);

    int quickUpdate(LambdaQueryWrapper<T> wrapper, T entity);

    int quickDelete(LambdaQueryWrapper<T> wrapper);
}
