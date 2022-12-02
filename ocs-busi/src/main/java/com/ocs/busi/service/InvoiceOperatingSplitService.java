package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.entity.InvoiceOperatingSplit;

/**
 * @author tangyx
 * @description 针对表【invoice_operating_split(经营性发票分录)】的数据库操作Service
 * @createDate 2022-11-09 14:27:40
 */
public interface InvoiceOperatingSplitService extends IService<InvoiceOperatingSplit> {

    Integer findPeriodNumMax(String accountingPeriod);

}
