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
package cn.odboy.modules.quartz.mapper;

import cn.odboy.modules.quartz.domain.QuartzLog;
import cn.odboy.modules.quartz.domain.vo.QuartzJobQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zheng Jie
 * @description
 * @date 2023-06-12
 **/
@Mapper
public interface QuartzLogMapper extends BaseMapper<QuartzLog> {
    IPage<QuartzLog> selectLogs(@Param("criteria") QuartzJobQueryArgs criteria, Page<Object> page);

    List<QuartzLog> selectLogs(@Param("criteria") QuartzJobQueryArgs criteria);
}
