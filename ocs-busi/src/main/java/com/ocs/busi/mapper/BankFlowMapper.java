package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.BankFlow;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface BankFlowMapper extends BaseMapper<BankFlow> {


    BankFlow findByDateIn(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    BankFlow findByBankSiteCode(@Param("bankSiteCode") String bankSiteCode);

    List<BankFlow> findAllInByBankSiteCode(@Param("bankSiteCodeStr") String bankSiteCodeStr);
}




