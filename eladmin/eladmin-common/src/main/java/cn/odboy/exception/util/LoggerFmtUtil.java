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
package cn.odboy.exception.util;

/**
 * 适配 logger 占位符
 *
 * @author odboy
 * @date 2025-01-13
 */
public class LoggerFmtUtil {
    /**
     * 替换字符串中的占位符 {} 为传入的参数
     *
     * @param message 消息模板
     * @param args    替换占位符的参数
     * @return 替换后的消息
     */
    public static String format(String message, Object... args) {
        if (message == null || args == null || args.length == 0) {
            return message;
        }
        // 使用 String.format 替换占位符
        return String.format(message.replace("{}", "%s"), args);
    }

    public static void main(String[] args) {
        // 示例用法
        String messageTemplate = "获取dingtalk accessToken失败, code={}, message={}";
        String formattedMessage = format(messageTemplate, 400, "Bad Request");
        // 输出: 获取dingtalk accessToken失败, code=400, message=Bad Request
        System.out.println(formattedMessage);
    }
}
