package com.ocs.busi.helper;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流水号生成助手类
 *
 * @author tangyixiang
 * @Date 2022/11/4
 */
public class SerialNumberHelper {

    // 初始值
    private AtomicInteger initialValue = new AtomicInteger();

    public SerialNumberHelper(String currentValue, String pattern) {
        if (StringUtils.isNotEmpty(currentValue)) {
            String[] array = currentValue.split(pattern);
            this.initialValue = new AtomicInteger(Integer.parseInt(array[array.length - 1]));
        }
    }

    public String generateNextId(String pattern, Integer length) {
        int i = initialValue.incrementAndGet();
        String nextId = StringUtils.leftPad(i + "", length, "0");
        return pattern + nextId;
    }


}
