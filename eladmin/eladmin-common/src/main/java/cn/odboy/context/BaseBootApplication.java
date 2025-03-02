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
package cn.odboy.context;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * 主要目的是输出应用的服务地址
 *
 * @author odboy
 * @date 2024-04-20
 */
@Slf4j
public class BaseBootApplication {
    @SneakyThrows
    protected static void initd(ConfigurableApplicationContext application) {
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = StrUtil.isEmpty(env.getProperty("server.servlet.context-path")) ? "" : env.getProperty("server.servlet.context-path");
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application is running! Access URLs:\n\t"
                        + "Local: \t\thttp://localhost:" + port + path + "/\n\t"
                        + "External: \thttp://" + ip + ":" + port + path + "/\n\t"
                        + "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n"
                        + "----------------------------------------------------------");
    }
}