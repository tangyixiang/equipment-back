package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.BankFlowLog;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.mapper.BankFlowLogMapper;
import com.ocs.busi.service.BankFlowLogService;
import com.ocs.common.constant.CommonConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author tangyx
 * @description 针对表【bank_flow_log(对账日志表)】的数据库操作Service实现
 * @createDate 2023-03-20 14:58:18
 */
@Service
public class BankFlowLogServiceImpl extends ServiceImpl<BankFlowLogMapper, BankFlowLog> implements BankFlowLogService {

    @Override
    public void addBankFlowUserLog(BankFlow bankFlow, CompanyReceivables companyReceivables, Double userAmount) {
        BankFlowLog bankFlowLog = new BankFlowLog();
        bankFlowLog.setId(IdUtil.getSnowflakeNextIdStr());
        bankFlowLog.setBankFlowId(bankFlow.getId());
        bankFlowLog.setReceivableId(companyReceivables.getId());
        bankFlowLog.setReceivableType(companyReceivables.getSourceType());
        bankFlowLog.setAmount(userAmount);
        bankFlowLog.setType(CommonConstants.AUTO_RECONCILIATION);
        bankFlowLog.setPeriod(companyReceivables.getPeriod());
        bankFlowLog.setCreateBy("system");
        bankFlowLog.setCreateTime(LocalDateTime.now());

        save(bankFlowLog);
    }
}




