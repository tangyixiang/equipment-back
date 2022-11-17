package com.ocs.busi.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ocs.busi.domain.entity.InvoiceOperating;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author tangyx
* @createDate 2022-11-07 11:58:55
* @Entity com.ocs.busi.domain.entity.InvoiceOperating
*/
public interface InvoiceOperatingMapper extends BaseMapper<InvoiceOperating> {

    InvoiceOperating findByFlowId(@Param("flowId") String flowId);

    List<String> groupByInvoicingPeriod();

    List<InvoiceOperating> findFlowIdIn(@Param("flowIds") String flowIds);
}




