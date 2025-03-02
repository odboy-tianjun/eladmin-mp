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

import cn.odboy.system.core.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2023-06-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> selectUsers(@Param("criteria") User.QueryArgs criteria);

    Long countByBlurry(@Param("criteria") User.QueryArgs criteria);

    User getByUsername(@Param("username") String username);

    User getByEmail(@Param("email") String email);

    User getByPhone(@Param("phone") String phone);

    void updatePwdByUsername(@Param("username") String username, @Param("password") String password, @Param("lastPwdResetTime") Date lastPwdResetTime);

    void updateEmailByUsername(@Param("username") String username, @Param("email") String email);

    List<User> selectUsersByRoleId(@Param("roleId") Long roleId);

    List<User> selectUsersByRoleDeptId(@Param("deptId") Long deptId);

    List<User> selectUsersByMenuId(@Param("menuId") Long menuId);

    int countByJobIds(@Param("jobIds") Set<Long> jobIds);

    int countByDeptIds(@Param("deptIds") Set<Long> deptIds);

    int countByRoleIds(@Param("roleIds") Set<Long> roleIds);

    void updatePwdByUserIds(@Param("userIds") Set<Long> userIds, @Param("pwd") String pwd);
}
