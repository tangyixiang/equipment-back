package com.ocs.busi.service.impl;

import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.dto.BankFlowUploadDto;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.BankFlowSplit;
import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.ocs.busi.helper.ExcelCellHelper;
import com.ocs.busi.helper.SerialNumberHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.BankFlowMapper;
import com.ocs.busi.mapper.BankFlowSplitMapper;
import com.ocs.busi.service.BankFlowService;
import com.ocs.busi.service.BankFlowSplitService;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tangyx
 * @description 针对表【bank_flow】的数据库操作Service实现
 * @createDate 2022-11-01 15:42:04
 */
@Service
public class BankFlowServiceImpl extends ServiceImpl<BankFlowMapper, BankFlow>
        implements BankFlowService {

    private static final Logger logger = LoggerFactory.getLogger(BankFlowServiceImpl.class);

    @Autowired
    private BankFlowSplitService bankFlowSplitService;
    @Autowired
    private BankFlowSplitMapper bankFlowSplitMapper;
    @Autowired
    private CompanyClientOrgService companyClientOrgService;

    @Override
    public Map<String, Object> uploadValidate(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto) {
        List<BankFlow> bankFlowList = convertExcelToFlow(inputStream, bankFlowUploadDto);
        // 所有的客户信息
        List<CompanyClientOrg> allCompanyOrg = companyClientOrgService.findAllCompanyOrg();
        logger.info("客户数量:{}", allCompanyOrg.size());
        Set<String> allCompanyOrgNameSet = allCompanyOrg.stream().map(CompanyClientOrg::getName).collect(Collectors.toSet());
        List<String> bankSiteCodeList = new ArrayList<>();
        // 根据BankSiteCode来查询
        String bankSiteCodeStr = bankFlowList.stream().map(flow -> flow.getBankSiteCode()).collect(Collectors.joining(","));
        List<BankFlow> bankFlows = getBaseMapper().findAllInByBankSiteCode(bankSiteCodeStr);

        bankFlows.forEach(flow -> bankSiteCodeList.add(flow.getBankSiteCode()));

        bankFlowList.forEach(flow -> {
            if (!allCompanyOrgNameSet.contains(flow.getAdversaryOrgName())) {
                throw new ServiceException("客户基础资料中不存在," + flow.getAdversaryOrgName());
            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("validate", bankFlows.size() == 0);
        result.put("message", bankSiteCodeList.toString());

        return result;

    }

    @Override
    @Transactional
    public synchronized void importBankFlow(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto) {
        List<BankFlow> bankFlowList = convertExcelToFlow(inputStream, bankFlowUploadDto);

        String idPattern = "YHSL" + DateUtils.dateTimeNow("yyyyMMdd");
        BankFlow todayLastDataFlow = Optional.ofNullable(getBaseMapper().findByDateIn(LocalDate.now(), LocalDate.now().plusDays(1))).orElse(new BankFlow());
        SerialNumberHelper serialNumberHelper = new SerialNumberHelper(todayLastDataFlow.getId(), idPattern);

        bankFlowList.forEach(bankFlow -> {
            bankFlow.setId(serialNumberHelper.generateNextId(idPattern, 4));

            BankFlow oldData = getBaseMapper().findByBankSiteCode(bankFlow.getBankSiteCode());
            if (oldData != null) {
                // 先删除拆分的数据
                List<BankFlowSplit> bankFlowSplitList = bankFlowSplitMapper.findByBankFlowId(oldData.getId());
                if (bankFlowSplitList.size() > 0) {
                    bankFlowSplitList.forEach(split -> split.setDel(CommonConstants.STATUS_DEL));
                    bankFlowSplitService.updateBatchById(bankFlowSplitList);
                    // TODO 根据拆分信息取消对账单
                }
                // TODO 取消对账单

            }

            saveOrUpdate(bankFlow);
        });
    }

    private List<BankFlow> convertExcelToFlow(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto) {
        List<BankFlow> bankFlowList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(bankFlowList, bankFlowUploadDto));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("银行流水导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板");
        }
        return bankFlowList;
    }

    private RowHandler createRowHandler(List<BankFlow> bankFlowList, BankFlowUploadDto bankFlowUploadDto) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }
            if (StringUtils.isEmpty(convertString(rowlist.get(1)))) {
                throw new ServiceException("凭证号为空，请检查导入模板");
            }
            String selfAccount = convertString(rowlist.get(1));
            LocalDateTime tradeTime = ExcelCellHelper.handleDate(rowlist.get(3) + "");

            boolean timeCondition = tradeTime.isBefore(LocalDateTime.of(bankFlowUploadDto.getEndDate(), LocalTime.MAX))
                    && tradeTime.isAfter(LocalDateTime.of(bankFlowUploadDto.getStartDate(), LocalTime.MIN));

            if (selfAccount.equals(bankFlowUploadDto.getAccount()) && timeCondition) {
                BankFlow bankFlow = new BankFlow();
                bankFlow.setBankSiteCode(convertString(rowlist.get(0)));
                bankFlow.setSelfAccount(convertString(rowlist.get(1)));
                bankFlow.setAdversaryAccount(convertString(rowlist.get(2)));
                bankFlow.setTradeTime(tradeTime);
                bankFlow.setTradeType(handleTradeType(convertString(rowlist.get(4))));
                String price = bankFlow.getTradeType().equals(CommonConstants.BORROW) ? convertString(rowlist.get(5)) : convertString(rowlist.get(6));
                bankFlow.setPrice(Double.parseDouble(price));
                bankFlow.setAdversaryBankCode(convertString(rowlist.get(7)));
                bankFlow.setSummary(convertString(rowlist.get(8)));
                bankFlow.setComment(convertString(rowlist.get(9)));
                bankFlow.setAdversaryOrgName(convertString(rowlist.get(10)));
                bankFlow.setBalance(convertString(rowlist.get(11)));
                bankFlow.setOtherInfo(convertString(rowlist.get(12)));
                bankFlow.setReconciliationFlag(CommonConstants.NOT_RECONCILED);
                bankFlow.setDel(CommonConstants.STATUS_NORMAL);

                ValidateHelper.validData(bankFlow);

                bankFlowList.add(bankFlow);
            }
        };
    }

    private String handleTradeType(String excelCellValue) {
        if (excelCellValue.equals("借")) {
            return CommonConstants.BORROW;
        } else if (excelCellValue.equals("贷")) {
            return CommonConstants.LOAN;
        } else {
            throw new ServiceException("请填写正确的 '借/贷' 数据");
        }
    }


    public String convertString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}




