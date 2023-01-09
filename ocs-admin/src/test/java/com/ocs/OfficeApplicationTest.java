package com.ocs;

import cn.hutool.core.map.BiMap;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ocs.web.controller.task.TaskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author tangyixiang
 * @Date 2022/10/13
 */
// @SpringBootTest
public class OfficeApplicationTest {

    private static final Log log = LogFactory.get();

    @Autowired
    DataSource dataSource;


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
    void getDbTableInfo() {
        Table table = MetaUtil.getTableMeta(dataSource, "apply_check");
        log.info("{}", table);
    }


    @Test
    void hutoolDemo() {
        BiMap<String, Object> map = new BiMap<>(new HashMap<>());
        map.put("a", 1);
        map.put("b", 2);
        log.info("获取的值:{}", map.getKey(2));
        String name = "张三";
        log.info("{}", MessageFormat.format("你好{0}", name));

    }

}
