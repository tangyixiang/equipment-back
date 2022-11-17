package com.ocs.common.utils.bean;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class BeanMapUtils {

    /**
     * 将对象属性转化为map结合
     */
    public static <T> Map<String, Object> convertDbQueryMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (ObjectUtils.isNotEmpty(beanMap.get(key))) {
                    map.put(StrUtil.toUnderlineCase(key + "") + "", beanMap.get(key));
                }
            }
        }
        return map;
    }

    /**
     * 将map集合中的数据转化为指定对象的同名属性中
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        T bean = clazz.newInstance();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
