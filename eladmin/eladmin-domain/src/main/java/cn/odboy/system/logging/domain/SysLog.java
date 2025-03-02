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
package cn.odboy.system.logging.domain;

import cn.odboy.mybatisplus.model.MyObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2018-11-24
 */
@Getter
@Setter
@NoArgsConstructor
@TableName("sys_log")
public class SysLog implements Serializable {
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long id;
    /**
     * 操作用户
     */
    private String username;
    /**
     * 描述
     */
    private String description;
    /**
     * 方法名
     */
    private String method;
    /**
     * 参数
     */
    private String params;
    /**
     * 日志类型
     */
    private String logType;
    /**
     * 请求ip
     */
    private String requestIp;
    /**
     * 地址
     */
    private String address;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 请求耗时
     */
    private Long time;
    /**
     * 异常详细
     */
    @JSONField(serialize = false)
    private String exceptionDetail;
    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    public SysLog(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class QueryArgs extends MyObject {
        private String blurry;
        private String username;
        private String logType;
        private List<Date> createTime;
    }
}
