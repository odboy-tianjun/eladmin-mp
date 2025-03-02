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
package cn.odboy.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.IdUtil;

import java.nio.charset.StandardCharsets;

/**
 * 加密令牌生成工具
 *
 * @author odboy
 * @date 2022-11-16
 */
public class Base64SecretUtil {
    private Base64SecretUtil() {
    }

    public static String build() {
        // 128位uuid
        return Base64Encoder.encode(getLongUuid(4).getBytes(StandardCharsets.UTF_8));
    }

    public static String getLongUuid(int bit) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bit; i++) {
            result.append(IdUtil.simpleUUID());
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(build());
    }
}
