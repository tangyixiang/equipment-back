package com.ocs;


import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.model.ReceivableBankFlowMapping;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.web.controller.task.TaskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author tangyixiang
 * @Date 2022/10/13
 */
@SpringBootTest
public class OfficeApplicationTest {

    private String SQL = "SELECT COLUMN_NAME AS dataIndex ,COLUMN_COMMENT AS title FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s' ORDER BY ordinal_position";

    @Autowired
    CompanyReceivablesService companyReceivablesService;

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

    // @Test
    void doubleTest(){

        CompanyReceivables companyReceivables = new CompanyReceivables();
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        companyReceivables.setId(snowflakeNextIdStr);

        ReceivableBankFlowMapping receivableBankFlowMapping = new ReceivableBankFlowMapping();
        receivableBankFlowMapping.setBankFlowId("1");
        receivableBankFlowMapping.setUsePrice(4324322d);

        companyReceivables.getRemark().add(receivableBankFlowMapping);

        companyReceivablesService.save(companyReceivables);

        CompanyReceivables receivables = companyReceivablesService.getById(snowflakeNextIdStr);
        for (ReceivableBankFlowMapping bankFlowMapping : receivables.getRemark()) {
            System.out.println(bankFlowMapping);
        }

    }

}
