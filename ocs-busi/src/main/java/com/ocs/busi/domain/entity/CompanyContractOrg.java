package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;


@Data
@TableName(value = "company_contract_org")
public class CompanyContractOrg extends SimpleEntity {
    /**
     * 合同ID
     */
    @TableId
    private String contractId;

    /**
     * 甲方组织ID
     */
    private String buyOrgId;

    /**
     * 甲方经办人
     */
    private String buyOrgContactName;

    /**
     * 甲方经办人电话
     */
    private String buyOrgContactPhone;

    /**
     * 乙方客户ID
     */
    private String sellOrgId;

    /**
     * 乙方经办人
     */
    private String sellOrgContactName;

    /**
     * 乙方经办人电话
     */
    private String sellOrgContactPhone;

    /**
     * 删除
     */
    private String del;

}
