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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Json工具
 *
 * @author odboy
 * @date 2024-10-01
 */
public class JsonUtil {
    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> kClass, Class<V> vClass) {
        return JSON.parseObject(jsonStr, new TypeReference<Map<K, V>>() {
        });
    }

    public static <V> List<V> toList(String jsonStr, Class<V> vClass) {
        return JSON.parseObject(jsonStr, new TypeReference<List<V>>() {
        });
    }

    public static void main(String[] args) {
        String mapStr = "{\"key1\":\"value1\", \"key2\":\"value2\"}";
        System.err.println(JsonUtil.toMap(mapStr, String.class, String.class));
        String listStr = "[\"value1\", \"value2\"]";
        System.err.println(JsonUtil.toList(listStr, String.class));
    }
}
