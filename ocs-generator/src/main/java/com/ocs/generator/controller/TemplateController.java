package com.ocs.generator.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.NamingCase;
import com.ocs.common.constant.Constants;
import com.ocs.generator.domain.GenDesc;
import com.ocs.generator.domain.GenTableColumn;
import com.ocs.generator.mapper.GenTableColumnMapper;
import com.ocs.generator.mapper.GenTableMapper;
import com.ocs.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;
    @Autowired
    private GenTableMapper genTableMapper;

    private String path = "D:\\codegen\\";

    public Map<String, String> templateMap = Map.of(
            "vm2/java/controller.java.vm", "controller.java",
            "vm2/js/api.js.vm", "api.js",
            "vm2/react/edit.jsx.vm", "edit.jsx",
            "vm2/react/table.jsx.vm", "table.jsx",
            "vm2/xml/mapper.xml.vm", "mapper.xml"
    );


    @GetMapping("/create")
    public String createByTable(GenDesc genDesc) {

        VelocityInitializer.initVelocity();
        VelocityContext context = new VelocityContext();
        Map<String, Object> map = BeanUtil.beanToMap(genDesc);

        genDesc.setSimpleClassName(Character.toLowerCase(genDesc.getClassName().charAt(0)) + genDesc.getClassName().substring(1));

        // 获取表的描述
        List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(genDesc.getTableName());

        genTableColumns.forEach(column -> {
            column.setJavaField(NamingCase.toCamelCase(column.getColumnName()));
            column.setJavaType("String");
            column.setQueryType("EQ");
            column.setIsQuery("1");
        });

        map.keySet().forEach(k -> context.put(k, map.get(k)));
        context.put("columnFields", genTableColumns);

        templateMap.keySet().forEach(templateName -> {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(templateName, Constants.UTF8);
            tpl.merge(context, sw);

            try {
                Files.writeString(Paths.get(path, templateMap.get(templateName)), sw.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return "ok";
    }
}
