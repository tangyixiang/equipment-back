package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 财政性发票分录
 *
 * @TableName invoice_finance_split
 */
@TableName(value = "invoice_finance_split")
@Data
public class InvoiceFinanceSplit extends InvoiceDataSplit {

    @TableId
    private String id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 发票ID
     */
    private String invoiceFinanceId;

    /**
     * 会计期间
     */
    private String period;


    private LocalDateTime createTime;

}
