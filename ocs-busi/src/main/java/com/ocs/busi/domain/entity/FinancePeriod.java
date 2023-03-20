package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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

    /**
     * 删除
     */
    private String del;
}