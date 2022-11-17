package com.ocs.busi.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author tangyixiang
 * @Date 2022/10/19
 */
@Data
public class CompanyContractDto {

    /**
     * 合同名称
     */
    private String name;

    /**
     * 合同编码
     */
    private String contractCode;

    /**
     * 合同类型 1 固定期  2 无固定期
     */
    private String contractType;

    /**
     * 合同签订开始日期
     */
    private LocalDate contractSignDateStart;

    /**
     * 合同签订结束日期
     */
    private LocalDate contractSignDateEnd;

    /**
     * 乙方客户ID
     */
    private String sellOrgId;


    /**
     * 状态  1 生效  2 失效
     */
    private String status;
}
