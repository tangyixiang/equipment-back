package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户扩展纬度
 *
 * @author tangyixiang
 * @Date 2022/10/27
 */
@Data
@TableName(autoResultMap = true)
public class SysUserExtension {

    @TableId
    private Long useId;

    // 1 在编 2 聘用
    private String hireType;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<InspectionQualification> certificate = new ArrayList<>();

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<CustomizeDimension> dimensions = new ArrayList<>();


    /**
     * 检测资质
     */
    @Data
    public static class InspectionQualification {

        private String id;

        private String inspection;

        private String code;

        private LocalDateTime startDate;

        private LocalDateTime endDate;

        private List<String> attachment;
    }


    /**
     * 扩展维度
     */
    @Data
    public static class CustomizeDimension {

        private String id;

        private String dimensionKey;

        private String dimensionValue;

    }
}
