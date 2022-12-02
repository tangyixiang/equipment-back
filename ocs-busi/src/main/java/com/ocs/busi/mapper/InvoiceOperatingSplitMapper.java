package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.InvoiceOperatingSplit;
import org.apache.ibatis.annotations.Param;

/**
 * @author tangyx
 * @description 针对表【invoice_operating_split(经营性发票分录)】的数据库操作Mapper
 * @createDate 2022-11-09 14:27:40
 * @Entity com.ocs.busi.domain.entity.InvoiceOperatingSplit
 */
public interface InvoiceOperatingSplitMapper extends BaseMapper<InvoiceOperatingSplit> {

    Integer findPeriodNumMax(@Param("period") String accountingPeriod);

}




