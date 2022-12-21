package com.ocs.busi.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BankFlowDto {
    /**
     * 流水ID
     */
    private String id;

    /**
     * 凭证号
     */
    private String bankSiteCode;

    /**
     * 本方账号
     */
    private String selfAccount;

    /**
     * 对方账号
     */
    private String adversaryAccount;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 1 借  2 贷
     */
    private String tradeType;

    /**
     * 对方行号
     */
    private String adversaryBankCode;

    /**
     * 用途
     */
    private String comment;

    /**
     * 对方单位名称
     */
    private String adversaryOrgName;


    /**
     * 个性化信息
     */
    private String otherInfo;

    /**
     * 对账标识  1 已对账  2 未对账
     */
    private String reconciliationFlag;

    /**
     * 对账类别  1 自动  2 手动
     */
    private String reconciliationModel;


    @TableField(exist = false)
    private List<LocalDate> tradeTimeArray;

    private LocalDateTime tradeTimeStart;

    private LocalDateTime tradeTimeEnd;

    @TableField(exist = false)
    private String associationIdStr;
}
