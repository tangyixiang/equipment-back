package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 对账日志表
 */
@Data
@TableName(value = "bank_flow_log")
public class BankFlowLog {

    @TableId
    private String id;

    /**
     * 银行流水ID
     */
    private String bankFlowId;

    /**
     * 应收账款ID
     */
    private String receivableId;

    /**
     * 开票日期
     */
    private LocalDate invoiceDate;

    /**
     * 客户名称
     */
    private String clientOrgName;

    /**
     * 账款类型 1 经营发票  2 财政发票
     */
    private String receivableType;

    /**
     * 金额
     */
    private Double amount;

    /**
     * 剩余银行金额
     */
    private Double unConfirmBankAmount;

    /**
     * 对账类型  1 自动对账  2 手动对账  3 取消对账
     */
    private String type;


    /**
     * 应收会计期
     */
    private String receivablePeriod;

    /**
     * 银行会计期间
     */
    private String bankPeriod;

    /**
     * 对账运行期间
     */
    private String period;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;


}