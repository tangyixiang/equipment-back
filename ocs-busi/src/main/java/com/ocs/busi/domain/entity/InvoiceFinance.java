package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 财政性发票
 * @TableName invoice_finance
 */
@TableName(value ="invoice_finance")
@Data
public class InvoiceFinance extends SimpleEntity {


    private String id;

    /**
     * 发票期间
     */
    @NotBlank(message = "发票期间不能为空")
    private String invoicingPeriod;

    /**
     * 开票日期
     */
    @NotBlank(message = "开票日期不能为空")
    private String invoicingDate;

    /**
     * 单位名称
     */
    @NotBlank(message = "单位名称不能为空")
    private String orgName;

    /**
     * 开票机构
     */
    @NotBlank(message = "开票机构不能为空")
    private String invoicingOrgName;

    /**
     * 票据编码
     */
    @NotBlank(message = "票据编码不能为空")
    private String invoiceBm;

    /**
     * 票据名称
     */
    @NotBlank(message = "票据名称不能为空")
    private String invoiceName;

    /**
     * 票据代码
     */
    @NotBlank(message = "票据代码不能为空")
    private String invoiceCode;

    /**
     * 票号
     */
    @TableId
    @NotBlank(message = "票号不能为空")
    private String invoiceId;

    /**
     * 缴款人
     */
    @NotBlank(message = "缴款人不能为空")
    private String payer;

    /**
     * 社会统一信用代码
     */
    private String socialCreditCode;

    /**
     * 已打印
     */
    @NotBlank(message = "已打印不能为空")
    private String printed;

    /**
     * 已开红票
     */
    @NotBlank(message = "已开红票不能为空")
    private String redInvoiceFlag;

    /**
     * 相关纸质票号
     */
    @NotBlank(message = "相关纸质票号不能为空")
    private String paperInvoiceNo;

    /**
     * 相关电子票号
     */
    @NotBlank(message = "相关电子票号不能为空")
    private String electronInvoiceNo;

    /**
     * 校验码
     */
    @NotBlank(message = "校验码不能为空")
    private String checkCode;

    /**
     * 编制人
     */
    @NotBlank(message = "编制人不能为空")
    private String creator;

    /**
     * 项目编码
     */
    @NotBlank(message = "项目编码不能为空")
    private String itemCode;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String itemName;

    /**
     * 计量单位
     */
    @NotBlank(message = "计量单位不能为空")
    private String unit;

    /**
     * 数量
     */
    @NotBlank(message = "数量不能为空")
    private String quantity;

    /**
     * 标准
     */
    @NotBlank(message = "标准不能为空")
    private String standard;

    /**
     * 金额
     */
    @NotBlank(message = "金额不能为空")
    private String price;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空")
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 分录生成标识
     */
    private boolean dataSplit;

    /**
     * 版本
     */
    private String version;

}
