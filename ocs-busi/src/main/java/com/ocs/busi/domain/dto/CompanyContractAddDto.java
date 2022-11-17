package com.ocs.busi.domain.dto;

import com.ocs.busi.domain.entity.CompanyContract;
import com.ocs.busi.domain.entity.CompanyContractItem;
import com.ocs.busi.domain.entity.CompanyContractOrg;
import lombok.Data;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/19
 */
@Data
public class CompanyContractAddDto {

    private CompanyContract contract;

    private CompanyContractOrg contractOrg;

    private List<CompanyContractItem> items;

}
