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
package cn.odboy.system.core.domain;

import cn.odboy.mybatisplus.model.MyEntity;
import cn.odboy.mybatisplus.model.MyObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@Getter
@Setter
@TableName("sys_user")
public class User extends MyEntity implements Serializable {
    @NotNull(groups = Update.class)
    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;
    @TableField(exist = false)
    @ApiModelProperty(value = "用户角色")
    private Set<Role> roles;
    @TableField(exist = false)
    @ApiModelProperty(value = "用户岗位")
    private Set<Job> jobs;
    @TableField(value = "dept_id")
    @ApiModelProperty(hidden = true)
    private Long deptId;
    @ApiModelProperty(value = "用户部门")
    @TableField(exist = false)
    private Dept dept;
    @NotBlank
    @ApiModelProperty(value = "用户名称")
    private String username;
    @NotBlank
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @Email
    @NotBlank
    @ApiModelProperty(value = "邮箱")
    private String email;
    @NotBlank
    @ApiModelProperty(value = "电话号码")
    private String phone;
    @ApiModelProperty(value = "用户性别")
    private String gender;
    @ApiModelProperty(value = "头像真实名称", hidden = true)
    private String avatarName;
    @ApiModelProperty(value = "头像存储的路径", hidden = true)
    private String avatarPath;
    @ApiModelProperty(value = "密码")
    private String password;
    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
    @ApiModelProperty(value = "是否为admin账号", hidden = true)
    private Boolean isAdmin = false;
    @ApiModelProperty(value = "最后修改密码的时间", hidden = true)
    private Date pwdResetTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class QueryArgs extends MyObject {
        private Long id;
        private Set<Long> deptIds = new HashSet<>();
        private String blurry;
        private Boolean enabled;
        private Long deptId;
        private List<Timestamp> createTime;
        private Long offset;
        private Long size;
    }
}