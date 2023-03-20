package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应收余额表明细来源
 *
 * @TableName company_receivable_balance_source
 */
@TableName(value = "company_receivable_balance_source")
@Data
public class ReceivableBalanceSource {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 余额表ID
     */
    private String balanceId;

    /**
     * 应收ID
     */
    private String receivableId;

    /**
     * 银行ID
     */
    private String flowId;

    /**
     * 来源类型  1 应收  2 银行
     */
    private String sourceType;

    /**
     * 删除 1 正常  2 删除
     */
    private String del;

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