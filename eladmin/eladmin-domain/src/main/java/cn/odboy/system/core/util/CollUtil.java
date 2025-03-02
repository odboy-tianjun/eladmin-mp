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
package cn.odboy.system.core.util;

import cn.odboy.system.core.model.MetaOptionModel;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 集合工具
 *
 * @author odboy
 * @date 2022-11-09
 */
public class CollUtil extends cn.hutool.core.collection.CollUtil {
    /**
     * 连接器
     */
    private static final Joiner JOINER = Joiner.on(",").skipNulls();
    /**
     * 分割器
     */
    private static final Splitter SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    public static String join(Object[] parts) {
        return JOINER.join(parts);
    }

    public static String join(Iterable<?> parts) {
        return JOINER.join(parts);
    }

    public static String join(Iterator<?> parts) {
        return JOINER.join(parts);
    }

    public static List<String> splitToList(String parts) {
        return SPLITTER.splitToList(parts);
    }

    /**
     * 分几份
     *
     * @param parts /
     * @param size  /
     * @param <T>   /
     * @return /
     */
    public static <T> List<List<T>> splitToSize(List<T> parts, int size) {
        return Lists.partition(parts, size);
    }

    /**
     * 一份几个
     *
     * @param parts    /
     * @param groupNum /
     * @param <T>      /
     * @return /
     */
    public static <T> List<List<T>> splitToSegment(List<T> parts, int groupNum) {
        List<List<T>> result = new ArrayList<>(1);
        int i = 0;
        for (; i < parts.size() / groupNum; i++) {
            result.add(parts.subList(i * groupNum, (i + 1) * groupNum));
        }
        if (parts.size() % groupNum != 0) {
            result.add(parts.subList(i * groupNum, i * groupNum + parts.size() % groupNum));
        }
        return result;
    }

    private static <T, K> Predicate<T> distinctPredicate(Function<? super T, ? extends K> keyMapper) {
        Map<Object, Boolean> map = new HashMap<>();
        return (t) -> null == map.putIfAbsent(keyMapper.apply(t), true);
    }

    /**
     * 流根据对象属性去重
     * <p>
     * 使用方式: list = CollUtil.streamDistinct(list, Object::getXXX)
     */
    public static <T, K> List<T> streamDistinct(List<T> data, Function<? super T, ? extends K> keyMapper) {
        return data.stream().filter(distinctPredicate(keyMapper)).collect(Collectors.toList());
    }

    /**
     * 流根据对象属性自然排序
     * <p>
     * 使用方式: list = CollUtil.streamAscSort(list, Object::getXXX)
     *
     * @param data         /
     * @param keyExtractor /
     * @param <T>          /
     * @param <U>          /
     * @return /
     */
    public static <T, U extends Comparable<? super U>> List<T> streamAscSort(List<T> data, Function<? super T, ? extends U> keyExtractor) {
        return data.stream().sorted(Comparator.comparing(keyExtractor)).collect(Collectors.toList());
    }

    /**
     * 流根据对象属性倒转排序
     * <p>
     * 使用方式: list = CollUtil.streamDescSort(list, Object::getXXX)
     *
     * @param data         /
     * @param keyExtractor /
     * @param <T>          /
     * @param <U>          /
     * @return /
     */
    public static <T, U extends Comparable<? super U>> List<T> streamDescSort(List<T> data, Function<? super T, ? extends U> keyExtractor) {
        return data.stream().sorted(Comparator.comparing(keyExtractor).reversed()).collect(Collectors.toList());
    }

    /**
     * 流拆分对象为键值对
     * <p>
     * 使用方式: map = CollUtil.streamToMap(list, Object::getXXKey, Object::getXXValue)
     *
     * @param data        /
     * @param keyMapper   /
     * @param valueMapper /
     * @param <T>         /
     * @param <K>         /
     * @param <U>         /
     * @return /
     */
    public static <T, K, U> Map<K, U> streamToMap(List<T> data, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        // 根据key去重再并流
        return streamDistinct(data, keyMapper).stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 流对List<Double>求和
     * <p>
     * 使用方式: total = CollUtil.streamDoubleAdd(doubleList)
     *
     * @param data /
     * @return /
     */
    public static Double streamDoubleAdd(List<Double> data) {
        if (CollUtil.isNotEmpty(data)) {
            return data.stream().filter(Objects::nonNull).reduce(Double::sum).orElse(0.0);
        }
        return 0.0;
    }

    /**
     * 流对List<BigDecimal>求和
     * <p>
     * 使用方式: total = CollUtil.streamBigDecimalAdd(doubleList)
     *
     * @param data /
     * @return /
     */
    public static BigDecimal streamBigDecimalAdd(List<BigDecimal> data) {
        if (CollUtil.isNotEmpty(data)) {
            return data.stream().filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 流对List<Object>中的某一BigDecimal属性求和
     * <p>
     * 使用方式: total = CollUtil.streamBigDecimalAdd(list, XXX)
     *
     * @param data /
     * @return /
     */
    public static <T> BigDecimal streamBigDecimalAdd(List<T> data, String elementName) {
        return data.stream().filter(Objects::nonNull).map(m -> {
            // 反射, 取集合中各元素的某属性
            BigDecimal that;
            try {
                Field field = m.getClass().getDeclaredField(elementName);
                field.setAccessible(true);
                that = (BigDecimal) field.get(m);
            } catch (Exception e) {
                // 忽略异常
                that = BigDecimal.ZERO;
            }
            return that;
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 流对List<BigDecimal>取最大值
     * <p>
     * 使用方式: max = CollUtil.streamBigDecimalMax(list)
     *
     * @param data /
     * @return /
     */
    public static BigDecimal streamBigDecimalMax(List<BigDecimal> data) {
        return data.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::max)
                .orElse(null);
    }

    /**
     * 流对List<BigDecimal>取最小值
     * <p>
     * 使用方式: max = CollUtil.streamBigDecimalMin(list)
     *
     * @param data /
     * @return /
     */
    public static BigDecimal streamBigDecimalMin(List<BigDecimal> data) {
        return data.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::min)
                .orElse(null);
    }

    /**
     * 流对List<BigDecimal>取平均值
     * <p>
     * 使用方式: avg = CollUtil.streamBigDecimalAvg(list)
     *
     * @param data /
     * @return /
     */
    public static BigDecimal streamBigDecimalAvg(List<BigDecimal> data) {
        return data.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(data.size()), 6, RoundingMode.FLOOR);
    }

    public static void main(String[] args) {
        List<MetaOptionModel> testData = new ArrayList<>();
        MetaOptionModel selectOption1 = MetaOptionModel.builder().label("苹果").value("apple").build();
        MetaOptionModel selectOption2 = MetaOptionModel.builder().label("苹果").value("apple").build();
        MetaOptionModel selectOption3 = MetaOptionModel.builder().label("香蕉").value("banana").build();
        MetaOptionModel selectOption4 = MetaOptionModel.builder().label("猫").value("cat").build();
        testData.add(selectOption1);
        testData.add(selectOption2);
        testData.add(selectOption3);
        testData.add(selectOption4);
        List<MetaOptionModel> selectOptions1 = CollUtil.streamAscSort(testData, MetaOptionModel::getValue);
        System.out.println(JSON.toJSONString(selectOptions1));
        List<MetaOptionModel> selectOptions2 = CollUtil.streamDescSort(testData, MetaOptionModel::getValue);
        System.out.println(JSON.toJSONString(selectOptions2));
        List<MetaOptionModel> selectOptions3 = CollUtil.streamDistinct(testData, MetaOptionModel::getValue);
        System.out.println(JSON.toJSONString(selectOptions3));
        Map<String, String> selectMap1 = CollUtil.streamToMap(testData, MetaOptionModel::getValue, MetaOptionModel::getLabel);
        System.out.println(JSON.toJSONString(selectMap1));
        // 这只是一个示范
        BigDecimal total1 = CollUtil.streamBigDecimalAdd(testData, "value");
        System.out.println(total1);
    }
}
