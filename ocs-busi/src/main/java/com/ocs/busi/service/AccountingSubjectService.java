package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.entity.AccountingSubject;

import java.util.Map;

/**
 * @author tangyx
 * @createDate 2022-11-07 17:52:26
 */
public interface AccountingSubjectService extends IService<AccountingSubject> {

    /**
     * 财政分录对应的科目
     *
     * @param itemName
     * @return
     */
    AccountingSubject findFinanceItemValue(String itemName);

    /**
     * 经营分录对应的科目map
     *
     * @param itemName
     * @return
     */
    Map<String, String> findOperatingData(String itemName);

}
