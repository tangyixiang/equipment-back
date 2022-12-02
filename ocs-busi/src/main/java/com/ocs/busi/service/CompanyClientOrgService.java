package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.entity.CompanyClientOrg;

import java.io.InputStream;
import java.util.List;


public interface CompanyClientOrgService extends IService<CompanyClientOrg> {

    void importOrg(InputStream inputStream);

    List<CompanyClientOrg> findAllCompanyOrg();
}
