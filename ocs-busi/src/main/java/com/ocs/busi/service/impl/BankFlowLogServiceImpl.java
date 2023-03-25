package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.BankFlowLog;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.mapper.BankFlowLogMapper;
import com.ocs.busi.service.BankFlowLogService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tangyx
 * @description 针对表【bank_flow_log(对账日志表)】的数据库操作Service实现
 * @createDate 2023-03-20 14:58:18
 */
@Service
public class BankFlowLogServiceImpl extends ServiceImpl<BankFlowLogMapper, BankFlowLog> implements BankFlowLogService {

    @Override
    public void addBankFlowUseLog(BankFlow bankFlow, CompanyReceivables companyReceivables, Double userAmount, String period) {
        BankFlowLog bankFlowLog = new BankFlowLog();
        bankFlowLog.setId(IdUtil.getSnowflakeNextIdStr());
        bankFlowLog.setBankFlowId(bankFlow.getId());
        bankFlowLog.setReceivableId(companyReceivables.getId());
        bankFlowLog.setInvoiceDate(companyReceivables.getInvoicingDate());
        bankFlowLog.setClientOrgName(companyReceivables.getClientOrgName());
        bankFlowLog.setReceivableType(companyReceivables.getSourceType());
        bankFlowLog.setAmount(userAmount);
        bankFlowLog.setUnConfirmBankAmount(bankFlow.getUnConfirmPrice());
        bankFlowLog.setReceivablePeriod(companyReceivables.getPeriod());
        bankFlowLog.setBankPeriod(bankFlow.getPeriod());
        bankFlowLog.setPeriod(period);
        bankFlowLog.setType(SecurityUtils.getUsername() == null ? CommonConstants.AUTO_RECONCILIATION : CommonConstants.MANUAL_RECONCILIATION);
        bankFlowLog.setCreateBy(SecurityUtils.getUsername() == null ? "system" : SecurityUtils.getUsername());
        bankFlowLog.setCreateTime(LocalDateTime.now());

        save(bankFlowLog);
    }

    @Override
    public void addBankFlowCancelLog(BankFlow bankFlow, CompanyReceivables companyReceivables, Double cancelAmount, String period) {
        BankFlowLog bankFlowLog = new BankFlowLog();
        bankFlowLog.setId(IdUtil.getSnowflakeNextIdStr());
        bankFlowLog.setBankFlowId(bankFlow.getId());
        bankFlowLog.setReceivableId(companyReceivables.getId());
        bankFlowLog.setClientOrgName(companyReceivables.getClientOrgName());
        bankFlowLog.setReceivableType(companyReceivables.getSourceType());
        bankFlowLog.setAmount(cancelAmount);
        bankFlowLog.setUnConfirmBankAmount(bankFlow.getUnConfirmPrice());
        bankFlowLog.setType(CommonConstants.CANCEL_RECONCILIATION);
        bankFlowLog.setReceivablePeriod(companyReceivables.getPeriod());
        bankFlowLog.setBankPeriod(bankFlow.getPeriod());
        bankFlowLog.setPeriod(period);
        bankFlowLog.setCreateBy(SecurityUtils.getUsername());
        bankFlowLog.setCreateTime(LocalDateTime.now());

        save(bankFlowLog);
    }

    @Override
    public List<BankFlowLog> findByPeriod(String period, String receivableType, String type) {
        return this.getBaseMapper().findByPeriod(period, receivableType, type);
    }
}




