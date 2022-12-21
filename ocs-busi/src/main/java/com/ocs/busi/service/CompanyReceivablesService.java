package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.entity.CompanyReceivables;

import java.io.InputStream;
import java.util.List;

/**
* @author tangyx
* @description 针对表【company_receivables】的数据库操作Service
* @createDate 2022-12-16 11:15:50
*/
public interface CompanyReceivablesService extends IService<CompanyReceivables> {

    void importReceivables(InputStream inputStream);

    void cancel(List<String> receivablesIds);
}
