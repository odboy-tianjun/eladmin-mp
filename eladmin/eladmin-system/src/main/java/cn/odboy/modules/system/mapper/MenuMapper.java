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
package cn.odboy.modules.system.mapper;

import cn.odboy.system.core.domain.Menu;
import cn.odboy.system.core.model.MenuQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2023-06-20
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> selectMenus(@Param("criteria") MenuQueryArgs criteria);

    int countByPid(@Param("pid") Long pid);

    void updateSubMenuCntById(@Param("count") int count, @Param("menuId") Long menuId);

    Menu getMenuByTitle(@Param("title") String title);

    Menu getMenuByComponentName(@Param("name") String name);

    LinkedHashSet<Menu> selectMenusByRoleIdsAndType(@Param("roleIds") Set<Long> roleIds, @Param("type") Integer type);

    List<Menu> selectMenusOrderByMenuSort();

    List<Menu> selectSubMenusByPid(@Param("pid") Long pid);
}
