package com.ocs.busi.service;

import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.BankFlowLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.entity.CompanyReceivables;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tangyx
 * @description 针对表【bank_flow_log(对账日志表)】的数据库操作Service
 * @createDate 2023-03-20 14:58:18
 */
public interface BankFlowLogService extends IService<BankFlowLog> {

    /**
     * 新增银行流水日志
     *
     * @param bankFlow           银行流水
     * @param companyReceivables 应收单
     * @param userAmount         金额
     */
    void addBankFlowUseLog(BankFlow bankFlow, CompanyReceivables companyReceivables, Double userAmount, String period);


    /**
     * 新增银行流水日志
     *
     * @param bankFlow           银行流水
     * @param companyReceivables 应收单
     * @param cancelAmount       金额
     */
    void addBankFlowCancelLog(BankFlow bankFlow, CompanyReceivables companyReceivables, Double cancelAmount, String period);

    List<BankFlowLog> findByPeriod(String period, String receivableType, String type);
}
