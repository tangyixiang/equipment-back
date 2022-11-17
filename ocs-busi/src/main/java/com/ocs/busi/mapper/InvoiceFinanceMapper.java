package com.ocs.busi.mapper;
import org.apache.ibatis.annotations.Param;

import com.ocs.busi.domain.entity.InvoiceFinance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author tangyx
* @description 针对表【invoice_finance(财政性发票)】的数据库操作Mapper
* @createDate 2022-11-07 11:58:55
* @Entity com.ocs.busi.domain.entity.InvoiceFinance
*/
public interface InvoiceFinanceMapper extends BaseMapper<InvoiceFinance> {

    List<String> groupByInvoicingPeriod();

    InvoiceFinance findByInvoiceId(@Param("invoiceId") String invoiceId);

    List<InvoiceFinance> findInvoiceIdIn(@Param("invoiceIds") String invoiceIds);
}




