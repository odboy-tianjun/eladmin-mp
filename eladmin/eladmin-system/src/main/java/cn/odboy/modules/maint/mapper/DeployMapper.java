/*
 *  Copyright 2019-2023 Zheng Jie
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
package cn.odboy.modules.maint.mapper;

import cn.odboy.system.maint.domain.Deploy;
import cn.odboy.system.maint.model.DeployQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @description
 * @date 2023-06-12
 **/
@Mapper
public interface DeployMapper extends BaseMapper<Deploy> {
    Long countByAppName(@Param("criteria") DeployQueryArgs criteria);

    List<Deploy> selectDeploys(@Param("criteria") DeployQueryArgs criteria);

    Set<Long> selectDeployIdsByAppIds(@Param("appIds") Set<Long> appIds);

    Deploy getDeployById(@Param("deployId") Long deployId);
}
