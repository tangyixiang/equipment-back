package com.ocs.busi.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CompanyReceivablesDto {

    private List<String> receivablesIds;

    private List<String> bankFlowIds;

    // 发票号码
    private String id;

    // 单据类型
    private String sourceType;

    // 期间
    private String period;

    // 开票日期开始
    private LocalDate invoiceStartDate;

    // 开票日期结束
    private LocalDate invoiceEndDate;

    // 金额起
    private Double amountStart;

    // 金额结束
    private Double amountEnd;

    // 客户名称
    private String clientOrgName;

    // 对账标识
    private String reconciliationFlag;

    // 对账模式
    private String reconciliationModel;

    private String associationIdStr;

}
