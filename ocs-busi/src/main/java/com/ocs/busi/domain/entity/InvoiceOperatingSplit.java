package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 经营性发票分录
 *
 * @TableName invoice_operating_split
 */
@TableName(value = "invoice_operating_split")
@Data
public class InvoiceOperatingSplit extends InvoiceDataSplit {

    @TableId(type = IdType.ASSIGN_ID)
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
    private String period;


    private LocalDateTime createTime;

}
