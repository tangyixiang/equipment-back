package com.ocs.common.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.common.constant.CommonConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

/**
 * @author tangyixiang
 * @Date 2022/10/13
 */
public class QueryHelper {

    /**
     * 动态生成查询条件(删除条件)
     *
     * @param bean
     * @param queryType 模糊查询 或者 精确查询
     * @param del       true 已删除    false 未删除的
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> dynamicCondition(T bean, String queryType, Boolean del) {
        QueryWrapper<T> queryWrapper = dynamicCondition(bean, queryType);
        queryWrapper.eq("del", del != null && !del ? CommonConstants.STATUS_NORMAL : CommonConstants.STATUS_DEL);
        return queryWrapper;
    }

    public static Map<String, Object> dynamicConditionMap(Object bean) {
        return BeanUtil.beanToMap(bean);
    }


    /**
     * 动态生成查询条件(不带删除)
     *
     * @param bean
     * @param queryType 模糊查询 或者 精确查询
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> dynamicCondition(T bean, String queryType) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (ObjectUtils.isNotEmpty(beanMap.get(key))) {
                    if (CommonConstants.QUERY_LIKE.equals(queryType)) {
                        queryWrapper.like(StrUtil.toUnderlineCase(key + ""), beanMap.get(key));
                    } else {
                        queryWrapper.eq(StrUtil.toUnderlineCase(key + ""), beanMap.get(key));
                    }
                }
            }
        }
        return queryWrapper;
    }

}
