package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.dto.BankFlowDto;
import com.ocs.busi.domain.entity.BankFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BankFlowMapper extends BaseMapper<BankFlow> {

    BankFlow findByDateIn(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<BankFlow> findAllInByBankSiteCode(@Param("bankSiteCodeStr") String bankSiteCodeStr);

    List<BankFlow> findByCondition(BankFlowDto bankFlowDto);

    List<BankFlow> countPriceBeforePeriod(@Param("period") String period);
}




