package com.ocs.busi.service;

import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.common.core.domain.model.LoginUser;

import java.io.InputStream;


public interface CompanyClientOrgService extends IService<CompanyClientOrg> {

    void importOrg(InputStream inputStream);
}
