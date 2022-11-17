package com.ocs.busi.service;

import com.ocs.busi.domain.entity.InvoiceFinanceSplit;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author tangyx
* @description 针对表【invoice_finance_split(经营性发票分录)】的数据库操作Service
* @createDate 2022-11-09 16:37:51
*/
public interface InvoiceFinanceSplitService extends IService<InvoiceFinanceSplit> {

    Integer findPeriodNumMax(String accountingPeriod);

}
