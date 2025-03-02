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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.context.CacheKey;
import cn.odboy.exception.BadRequestException;
import cn.odboy.exception.EntityExistException;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.security.model.AuthorityDto;
import cn.odboy.system.core.domain.Menu;
import cn.odboy.system.core.domain.Role;
import cn.odboy.system.core.domain.User;
import cn.odboy.system.core.model.RoleQueryArgs;
import cn.odboy.modules.system.mapper.RoleDeptMapper;
import cn.odboy.modules.system.mapper.RoleMapper;
import cn.odboy.modules.system.mapper.RoleMenuMapper;
import cn.odboy.modules.system.mapper.UserMapper;
import cn.odboy.system.core.service.RoleService;
import cn.odboy.util.FileUtil;
import cn.odboy.util.PageUtil;
import cn.odboy.util.RedisUtil;
import cn.odboy.util.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "role")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    private final RoleMapper roleMapper;
    private final RoleDeptMapper roleDeptMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RedisUtil redisUtil;
    private final UserMapper userMapper;

    @Override
    public List<Role> queryAll() {
        return roleMapper.selectMetaRoles();
    }

    @Override
    public List<Role> queryAll(RoleQueryArgs criteria) {
        return roleMapper.selectRoles(criteria);
    }

    @Override
    public PageResult<Role> queryAll(RoleQueryArgs criteria, Page<Object> page) {
        criteria.setOffset(page.offset());
        List<Role> roles = roleMapper.selectRoles(criteria);
        Long total = roleMapper.countByBlurry(criteria);
        return PageUtil.toPage(roles, total);
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public Role findById(long id) {
        return roleMapper.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Role resources) {
        if (roleMapper.getByName(resources.getName()) != null) {
            throw new EntityExistException(Role.class, "username", resources.getName());
        }
        save(resources);
        // 判断是否有部门数据，若有，则需创建关联
        if (CollectionUtil.isNotEmpty(resources.getDepts())) {
            roleDeptMapper.insertRoleDept(resources.getId(), resources.getDepts());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role resources) {
        Role role = getById(resources.getId());
        Role role1 = roleMapper.getByName(resources.getName());
        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(Role.class, "username", resources.getName());
        }
        role.setName(resources.getName());
        role.setDescription(resources.getDescription());
        role.setDataScope(resources.getDataScope());
        role.setDepts(resources.getDepts());
        role.setLevel(resources.getLevel());
        // 更新
        saveOrUpdate(role);
        // 删除关联部门数据
        roleDeptMapper.deleteByRoleId(resources.getId());
        // 判断是否有部门数据，若有，则需更新关联
        if (CollectionUtil.isNotEmpty(resources.getDepts())) {
            roleDeptMapper.insertRoleDept(resources.getId(), resources.getDepts());
        }
        // 更新相关缓存
        delCaches(role.getId(), null);
    }

    @Override
    public void updateMenu(Role role) {
        List<User> users = userMapper.selectUsersByRoleId(role.getId());
        // 更新菜单
        roleMenuMapper.deleteByRoleId(role.getId());
        // 判断是否为空
        if (CollUtil.isNotEmpty(role.getMenus())) {
            roleMenuMapper.insertRoleMenu(role.getId(), role.getMenus());
        }
        // 更新缓存
        delCaches(role.getId(), users);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            // 更新相关缓存
            delCaches(id, null);
        }
        removeBatchByIds(ids);
        // 删除角色部门关联数据、角色菜单关联数据
        roleDeptMapper.deleteByRoleIds(ids);
        roleMenuMapper.deleteByRoleIds(ids);
    }

    @Override
    public List<Role> findByUsersId(Long userId) {
        String key = CacheKey.ROLE_USER + userId;
        List<Role> roles = redisUtil.getList(key, Role.class);
        if (CollUtil.isEmpty(roles)) {
            roles = roleMapper.selectRolesByUserId(userId);
            redisUtil.set(key, roles, 1, TimeUnit.DAYS);
        }
        return roles;
    }

    @Override
    public Integer findByRoles(Set<Role> roles) {
        if (roles.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        Set<Role> roleSet = new HashSet<>();
        for (Role role : roles) {
            roleSet.add(findById(role.getId()));
        }
        return Collections.min(roleSet.stream().map(Role::getLevel).collect(Collectors.toList()));
    }

    @Override
    @Cacheable(key = "'auth:' + #p0.id")
    public List<AuthorityDto> mapToGrantedAuthorities(User user) {
        Set<String> permissions = new HashSet<>();
        // 如果是管理员直接返回
        if (user.getIsAdmin()) {
            permissions.add("admin");
            return permissions.stream().map(AuthorityDto::new)
                    .collect(Collectors.toList());
        }
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        permissions = roles.stream().flatMap(role -> role.getMenus().stream())
                .map(Menu::getPermission)
                .filter(StringUtil::isNotBlank).collect(Collectors.toSet());
        return permissions.stream().map(AuthorityDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void download(List<Role> roles, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Role role : roles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("角色名称", role.getName());
            map.put("角色级别", role.getLevel());
            map.put("描述", role.getDescription());
            map.put("创建日期", role.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userMapper.countByRoleIds(ids) > 0) {
            throw new BadRequestException("所选角色存在用户关联，请解除关联再试！");
        }
    }

    @Override
    public List<Role> findByMenuId(Long menuId) {
        return roleMapper.selectRolesByMenuId(menuId);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, List<User> users) {
        users = CollectionUtil.isEmpty(users) ? userMapper.selectUsersByRoleId(id) : users;
        if (CollectionUtil.isNotEmpty(users)) {
            Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
            redisUtil.delByKeys(CacheKey.DATA_USER, userIds);
            redisUtil.delByKeys(CacheKey.MENU_USER, userIds);
            redisUtil.delByKeys(CacheKey.ROLE_AUTH, userIds);
            redisUtil.delByKeys(CacheKey.ROLE_USER, userIds);

        }
        redisUtil.del(CacheKey.ROLE_ID + id);
    }
}
