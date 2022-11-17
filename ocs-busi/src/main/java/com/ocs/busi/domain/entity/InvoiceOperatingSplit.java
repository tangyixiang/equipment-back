package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.ocs.common.annotation.Excel;
import lombok.Data;

/**
 * 经营性发票分录
 * @TableName invoice_operating_split
 */
@TableName(value ="invoice_operating_split")
@Data
public class InvoiceOperatingSplit extends InvoiceDataSplit {

    @TableId
    private String id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 发票ID
     */
    private String invoiceOperatingId;

    /**
     * 会计期间
     */
    private String accountingPeriod;

    /**
     * 同一会计期间运行了几次
     */
    private Integer periodNum;

    private LocalDateTime createTime;

}
