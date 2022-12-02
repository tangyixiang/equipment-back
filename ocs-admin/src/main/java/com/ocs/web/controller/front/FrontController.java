package com.ocs.web.controller.front;

import cn.hutool.core.text.NamingCase;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/front")
public class FrontController {

    @GetMapping("/column")
    public Object getTableColumn(String tableName) {
        String SQL = "SELECT COLUMN_NAME AS dataIndex ,COLUMN_COMMENT AS title FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s' ORDER BY ordinal_position";

        String sql = String.format(SQL, tableName);
        List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
        List<Map<String,Object>> list = new ArrayList<>();
        maps.forEach(map -> {
            Set<String> strings = map.keySet();
            TreeMap<String, Object> hashMap = new TreeMap<>();
            for (String key : strings) {
                hashMap.put(key, NamingCase.toCamelCase((String)map.get(key)));
            }
            hashMap.put("valueType", "text");
            list.add(hashMap);
        });

        return list;
    }


}
