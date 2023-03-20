package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对账余额表
 *
 * @TableName company_receivable_balance
 */
@TableName(value = "company_receivable_balance")
@Data
public class ReceivableBalance {

    @TableField
    private String id;

    /**
     * 上期的余额ID
     */
    private String lastBalanceId;

    /**
     * 客户名称
     */
    private String clientOrgName;

    /**
     * 会计期间
     */
    private String period;

    /**
     * 余额类型 1 经营性  2 财政性
     */
    private String type;

    /**
     * 应收余额
     */
    private Double receivableAmount;

    /**
     * 预收余额
     */
    private Double flowAmount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    protected LocalDateTime updateTime;

}