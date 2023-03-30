package com.ocs.busi.service.impl;

import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.dto.BankFlowUploadDto;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.helper.ExcelCellHelper;
import com.ocs.busi.helper.SerialNumberHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.BankFlowMapper;
import com.ocs.busi.service.BankFlowService;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 银行流水service
 */
@Slf4j
@Service
public class BankFlowServiceImpl extends ServiceImpl<BankFlowMapper, BankFlow> implements BankFlowService {

    @Autowired
    private CompanyClientOrgService companyClientOrgService;
    @Autowired
    private CompanyReceivablesService companyReceivablesService;

    @Override
    public Map<String, Object> uploadValidate(InputStream inputStream, BankFlowUploadDto uploadDto) {
        // 尝试转换数据
        convertExcelToFlow(inputStream, uploadDto);
        // 按会计期+账号+时间段导入银行流水。
        Long count = lambdaQuery().eq(BankFlow::getPeriod, uploadDto.getPeriod()).eq(BankFlow::getSelfAccount, uploadDto.getAccount())
                .le(BankFlow::getTradeTime, uploadDto.getEndDate()).ge(BankFlow::getTradeTime, uploadDto.getStartDate()).count();

        Map<String, Object> result = new HashMap<>();
        result.put("validate", count == 0);

        return result;

    }

    @Override
    @Transactional
    public synchronized void importBankFlow(InputStream inputStream, BankFlowUploadDto uploadDto) {
        List<BankFlow> bankFlowList = convertExcelToFlow(inputStream, uploadDto);

        Long count = lambdaQuery().eq(BankFlow::getPeriod, uploadDto.getPeriod()).eq(BankFlow::getSelfAccount, uploadDto.getAccount())
                .le(BankFlow::getTradeTime, uploadDto.getEndDate()).ge(BankFlow::getTradeTime, uploadDto.getStartDate())
                .in(BankFlow::getReconciliationFlag, List.of(CommonConstants.PART_RECONCILED, CommonConstants.RECONCILED)).count();

        if (count > 0) {
            throw new ServiceException("部分银行流水已对账回款,请先取消");
        }
        String idPattern = "YHSL" + DateUtils.dateTimeNow("yyyyMMdd");
        BankFlow todayLastDataFlow = Optional.ofNullable(getBaseMapper().findByDateIn(LocalDate.now(), LocalDate.now().plusDays(1))).orElse(new BankFlow());
        SerialNumberHelper serialNumberHelper = new SerialNumberHelper(todayLastDataFlow.getId(), idPattern);
        bankFlowList.forEach(bankFlow -> bankFlow.setId(serialNumberHelper.generateNextId(idPattern, 4)));
        saveBatch(bankFlowList);
    }

    @Override
    public List<BankFlow> countPriceBeforePeriod(String period) {
        return this.getBaseMapper().countPriceBeforePeriod(period);
    }

    private List<BankFlow> convertExcelToFlow(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto) {
        List<BankFlow> bankFlowList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(bankFlowList, bankFlowUploadDto));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            log.error("银行流水导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板," + e.getMessage());
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

            boolean timeCondition = tradeTime.isBefore(LocalDateTime.of(bankFlowUploadDto.getEndDate(), LocalTime.of(23, 59, 59)))
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
                bankFlow.setConfirmPrice(0d);
                bankFlow.setUnConfirmPrice(Double.parseDouble(price));
                bankFlow.setAdversaryBankCode(convertString(rowlist.get(7)));
                bankFlow.setSummary(convertString(rowlist.get(8)));
                bankFlow.setComment(convertString(rowlist.get(9)));
                bankFlow.setAdversaryOrgName(convertString(rowlist.get(10)));
                bankFlow.setBalance(convertString(rowlist.get(11)));
                bankFlow.setOtherInfo(convertString(rowlist.get(12)));
                bankFlow.setReconciliationFlag(CommonConstants.NOT_RECONCILED);
                bankFlow.setDel(CommonConstants.STATUS_NORMAL);
                bankFlow.setPeriod(bankFlowUploadDto.getPeriod());

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




