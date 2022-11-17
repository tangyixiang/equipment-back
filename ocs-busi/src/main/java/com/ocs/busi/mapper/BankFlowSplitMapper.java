package com.ocs.busi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ocs.busi.domain.entity.BankFlowSplit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

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




