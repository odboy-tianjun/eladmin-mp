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

import cn.odboy.system.core.domain.Role;
import cn.odboy.system.core.model.RoleQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2023-06-20
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> selectMetaRoles();

    Role getById(@Param("roleId") Long roleId);

    Role getByName(@Param("name") String name);

    List<Role> selectRoles(@Param("criteria") RoleQueryArgs criteria);

    Long countByBlurry(@Param("criteria") RoleQueryArgs criteria);

    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    int countByDeptIds(@Param("deptIds") Set<Long> deptIds);

    List<Role> selectRolesByMenuId(@Param("menuId") Long menuId);
}
