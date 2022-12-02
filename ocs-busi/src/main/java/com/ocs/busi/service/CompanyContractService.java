package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.dto.CompanyContractAddDto;
import com.ocs.busi.domain.entity.CompanyContract;


public interface CompanyContractService extends IService<CompanyContract> {

    void save(CompanyContractAddDto companyContractAddDto);

    void updateContract(CompanyContractAddDto companyContractAddDto);
}
