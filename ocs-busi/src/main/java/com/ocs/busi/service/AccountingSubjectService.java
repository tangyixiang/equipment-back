package com.ocs.busi.service;

import com.ocs.busi.domain.dto.AccountingSubjectDto;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author tangyx
* @createDate 2022-11-07 17:52:26
*/
public interface AccountingSubjectService extends IService<AccountingSubject> {


    List<AccountingSubjectDto> getAccountSubjectTree();

    List<AccountingSubjectDto> findSubjectChild(String name);
}
