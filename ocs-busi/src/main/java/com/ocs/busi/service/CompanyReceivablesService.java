package com.ocs.busi.service;

import com.ocs.busi.domain.entity.CompanyReceivables;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;

/**
* @author tangyx
* @description 针对表【company_receivables】的数据库操作Service
* @createDate 2022-12-16 11:15:50
*/
public interface CompanyReceivablesService extends IService<CompanyReceivables> {

    void importReceivables(InputStream inputStream);
}
