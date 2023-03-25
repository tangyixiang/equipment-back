package com.ocs.busi.mapper;

import com.ocs.busi.domain.entity.BankFlowLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【bank_flow_log(对账日志表)】的数据库操作Mapper
 * @createDate 2023-03-20 14:58:18
 * @Entity com.ocs.busi.domain.entity.BankFlowLog
 */
public interface BankFlowLogMapper extends BaseMapper<BankFlowLog> {

    List<BankFlowLog> findByPeriod(@Param("period") String period, @Param("receivableType") String receivableType, @Param("type") String type);
}




