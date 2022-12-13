package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @TableName bank_flow
 */
@Data
@TableName(value = "bank_flow")
public class BankFlow extends SimpleEntity {
    /**
     * 流水ID
     */
    private String id;

    /**
     * 凭证号
     */
    @TableId
    @NotBlank(message = "凭证号不能为空")
    private String bankSiteCode;

    /**
     * 本方账号
     */
    @NotBlank(message = "本方账号不能为空")
    private String selfAccount;

    /**
     * 对方账号
     */
    private String adversaryAccount;

    /**
     * 交易时间
     */
    @NotNull(message = "交易时间不能为空")
    private LocalDateTime tradeTime;

    /**
     * 1 借  2 贷
     */
    @NotBlank(message = "借/贷不能为空")
    private String tradeType;

    /**
     * 金额
     */
    private Double price;


    /**
     * 对方行号
     */
    private String adversaryBankCode;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 用途
     */
    private String comment;

    /**
     * 对方单位名称
     */
    private String adversaryOrgName;

    /**
     * 余额
     */
    private String balance;

    /**
     * 个性化信息
     */
    private String otherInfo;

    /**
     * 对账标识  Y 已对账  N 未对账
     */
    private String reconciliationFlag;

    /**
     * 对账类别  1 自动  2 手动
     */
    private String reconciliationModel;

    /**
     * 关联对账ID
     */
    private String associationId;

    /**
     * 数据是否拆分
     */
    private Boolean dataSplit;

    private String del;

    @TableField(exist = false)
    private List<LocalDate> tradeTimeArray;
}
