package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.InvoiceFinanceSplit;

/**
 * @author tangyx
 * @description 针对表【invoice_finance_split(经营性发票分录)】的数据库操作Mapper
 * @createDate 2022-11-09 16:37:51
 * @Entity com.ocs.busi.domain.entity.InvoiceFinanceSplit
 */
public interface InvoiceFinanceSplitMapper extends BaseMapper<InvoiceFinanceSplit> {

    Integer findPeriodNumMax(String accountingPeriod);

}




