package com.ocs.generator.domain;

import lombok.Data;

@Data
public class GenDesc {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 基本包
     */
    private String basePackage = "com.ocs.web";

    /**
     * 类放的位置
     */
    private String packageName;

    /**
     * 类名字
     */
    private String ClassName;

    /**
     * 类属性名字
     */
    private String simpleClassName;

    /**
     * 模块名字
     */
    private String moduleName;

    /**
     * modal标题
     */
    private String modalTitle;

    /**
     * url 路径名称
     */
    private String apiPath;
}
