package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.InvoiceOperating;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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




