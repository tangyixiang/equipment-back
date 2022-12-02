package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.BankFlowSplit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangyx
 * @description 针对表【bank_flow_split(银行流水拆分)】的数据库操作Mapper
 * @createDate 2022-11-04 15:53:28
 * @Entity com.ocs.busi.domain.entity.BankFlowSplit
 */
@Repository
public interface BankFlowSplitMapper extends BaseMapper<BankFlowSplit> {

    List<BankFlowSplit> findByBankFlowId(@Param("bankFlowId") String bankFlowId);

}




