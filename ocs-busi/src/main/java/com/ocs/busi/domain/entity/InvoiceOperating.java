package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 经营性发票
 *
 * @TableName invoice_operating
 */
@TableName(value = "invoice_operating")
@Data
public class InvoiceOperating extends SimpleEntity {


    private String id;

    /**
     * 开票期间
     */
    @NotBlank(message = "开票期间不能为空")
    private String invoicingPeriod;

    /**
     * 流水号
     */
    @TableId
    @NotBlank(message = "流水号不能为空")
    private String flowId;

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderId;

    /**
     * 发票创建时间
     */
    @NotNull(message = "发票创建时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invoiceCreateTime;

    /**
     * 开票时间
     */
    @NotNull(message = "开票时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invoicingTime;

    /**
     * 红票标志
     */
    @NotBlank(message = "红票标志不能为空")
    private String redInvoice;

    /**
     * 发票种类
     */
    @NotBlank(message = "发票种类不能为空")
    private String invoiceType;

    /**
     * 发票代码
     */
    @NotBlank(message = "发票代码不能为空")
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceId;

    /**
     * 购方名称
     */
    private String buyerName;

    /**
     * 购方税号
     */
    private String buyerTaxId;

    /**
     * 购方手机号
     */
    private String buyerPhone;

    /**
     * 购方邮箱
     */
    private String buyerEmail;

    /**
     * 购方开户行及账号
     */
    private String buyerBankInfo;

    /**
     * 购方地址、电话
     */
    private String buyerAddressInfo;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    /**
     * 商品编码
     */
    @NotBlank(message = "商品编码不能为空")
    private String productCode;

    /**
     * 规格型号
     */
    private String specifications;

    /**
     * 单位
     */
    @NotBlank(message = "商品编码不能为空")
    private String unit;

    /**
     * 数量
     */
    @NotBlank(message = "数量不能为空")
    private String quantity;

    /**
     * 含税单价
     */
    @NotBlank(message = "含税单价不能为空")
    private String unitPriceIncludingTax;

    /**
     * 税率
     */
    @NotBlank(message = "税率不能为空")
    private String taxRate;

    /**
     * 含税金额
     */
    @NotBlank(message = "含税金额不能为空")
    private String priceIncludingTax;

    /**
     * 不含税金额
     */
    @NotBlank(message = "不含税金额不能为空")
    private String priceExcludingTax;

    /**
     * 税额
     */
    @NotBlank(message = "税额不能为空")
    private String taxPrice;

    /**
     * 合计含税金额
     */
    @NotBlank(message = "合计含税金额不能为空")
    private String totalPriceIncludingTax;

    /**
     * 合计不含税金额
     */
    @NotBlank(message = "合计不含税金额不能为空")
    private String totalPriceExcludingTax;

    /**
     * 合计税额
     */
    @NotBlank(message = "合计税额不能为空")
    private String totalTaxPrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 开票员
     */
    @NotBlank(message = "开票员不能为空")
    private String billingStaff;

    /**
     * 收款人
     */
    @NotBlank(message = "收款人不能为空")
    private String payee;

    /**
     * 复核人
     */
    @NotBlank(message = "复核人不能为空")
    private String reviewer;

    /**
     * 部门门店
     */
    @NotBlank(message = "部门门店不能为空")
    private String store;

    /**
     * 开票方式
     */
    @NotBlank(message = "开票方式不能为空")
    private String invoicingMethod;

    /**
     * PDF地址
     */
    @NotBlank(message = "PDF地址不能为空")
    private String pdfPath;

    /**
     * 开票状态
     */
    @NotBlank(message = "开票状态不能为空")
    private String invoiceState;

    /**
     * 特殊票种
     */
    private String specialInvoice;

    /**
     * 清单标志
     */
    @NotBlank(message = "清单标志不能为空")
    private String clearFlag;

    /**
     * 分机号
     */
    @NotBlank(message = "分机号不能为空")
    private String extCode;

    /**
     * 机器编号
     */
    @NotBlank(message = "机器编号不能为空")
    private String machineCode;

    /**
     * 终端号
     */
    private String terminalCode;

    /**
     * 校验码
     */
    @NotBlank(message = "校验码不能为空")
    private String checkCode;

    /**
     * 单价（不含税）
     */
    @NotBlank(message = "单价（不含税）不能为空")
    private String unitPriceExcludingTax;

    /**
     * 作废人/时间
     */
    private String invalidInfo;

    /**
     * 交付手机
     */
    private String receiverPhone;

    /**
     * 交付邮箱
     */
    private String receiverEmail;

    /**
     * 车架号/车辆识别代码
     */
    private String carInfo;

    /**
     * 经营/拍卖单位名称
     */
    private String auctionName;

    /**
     * 经营/拍卖单位地址
     */
    private String auctionAddress;

    /**
     * 经营/拍卖单位税号
     */
    private String auctionTaxNo;

    /**
     * 经营/拍卖单位电话
     */
    private String auctionPhone;

    /**
     * 开户行、账号
     */
    private String bankInfo;

    /**
     * 分录生成标识
     */
    private Boolean dataSplit;

    /**
     * 版本号
     */
    private String version;

}
