package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @TableName finance_period
 */
@TableName(value = "finance_period")
@Data
public class FinancePeriod extends SimpleEntity {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 会计期间
     */
    @NotBlank(message = "会计期间不能为空")
    private String period;


    @NotNull(message = "凭证号起始值不能为空")
    private String value;

    // 1 经营 2 财务
    @NotNull(message = "类型不能为空")
    private String type;

    private boolean open;

    /**
     * 删除
     */
    private String del;
}