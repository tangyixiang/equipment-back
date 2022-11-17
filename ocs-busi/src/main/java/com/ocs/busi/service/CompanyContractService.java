package com.ocs.busi.service;

import com.ocs.busi.domain.dto.CompanyContractAddDto;
import com.ocs.busi.domain.entity.CompanyContract;
import com.baomidou.mybatisplus.extension.service.IService;


public interface CompanyContractService extends IService<CompanyContract> {

    void save(CompanyContractAddDto companyContractAddDto);

    void updateContract(CompanyContractAddDto companyContractAddDto);
}
