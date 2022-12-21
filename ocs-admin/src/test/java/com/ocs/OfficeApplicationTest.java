package com.ocs;


import cn.hutool.core.text.NamingCase;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.ocs.web.controller.task.TaskController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author tangyixiang
 * @Date 2022/10/13
 */
// @SpringBootTest
public class OfficeApplicationTest {

    private String SQL = "SELECT COLUMN_NAME AS dataIndex ,COLUMN_COMMENT AS title FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s' ORDER BY ordinal_position";

    @Test
    public void dbConvert() {
        String tableName = "employee_salary";
        String sql = String.format(SQL, tableName);
        List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        maps.forEach(map -> {
            Set<String> strings = map.keySet();
            HashMap<String, Object> hashMap = new HashMap<>();
            for (String key : strings) {
                hashMap.put(key, NamingCase.toCamelCase((String) map.get(key)));
            }
            hashMap.put("valueType", "text");
            list.add(hashMap);
        });

        System.out.println("=============");
        System.out.println(JSON.toJSONString(list));
        System.out.println("=============");

    }

    @Test
    void getParameterName() throws Exception {
        Class cls = TaskController.class;
        Method method = cls.getMethod("test", String.class, Integer.class);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println(parameter.getName());
        }
    }

    @Test
    void doubleTest(){
        System.out.println(new BigDecimal(3540d).compareTo(new BigDecimal(3540d)));
    }

}
