package com.ocs.busi.task.handle;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ocs.busi.domain.entity.*;
import com.ocs.busi.service.*;
import com.ocs.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Slf4j
@Component
public class InvoiceOperatingHandle {

    @Autowired
    private AccountingSubjectService accountingSubjectService;
    @Autowired
    private InvoiceOperatingSplitService invoiceOperatingSplitService;
    @Autowired
    private InvoiceOperatingService invoiceOperatingService;
    @Autowired
    private BankFlowLogService bankFlowLogService;
    @Autowired
    private BankFlowService bankFlowService;

    /**
     * 发票拆分
     *
     * @param startCertificateId 起始凭照号ID
     * @param receivablesList    发票数据
     * @param period             会计期间
     * @param taskId             任务ID
     * @return
     */
    @Transactional
    public Integer dataSplit(Integer startCertificateId, List<CompanyReceivables> receivablesList, String period, String taskId) {
        // 初始化凭照号码起始值
        AtomicInteger atomicInteger = new AtomicInteger(startCertificateId);

        Map<String, List<CompanyReceivables>> collect = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getInvoiceId));

        for (String invoiceId : collect.keySet()) {
            log.info("开始处理发票ID:{}", invoiceId);
            List<CompanyReceivables> list = collect.get(invoiceId);
            List<String> idList = list.stream().map(CompanyReceivables::getId).collect(Collectors.toList());
            List<InvoiceOperating> invoiceOperatingList = invoiceOperatingService.listByIds(idList);
            List<Double> priceList = computeTotalPrice(invoiceOperatingList);
            int increment = atomicInteger.incrementAndGet();
            InvoiceOperating firstInvoice = invoiceOperatingList.get(0);
            String date = LocalDateTimeUtil.format(firstInvoice.getInvoicingTime().toLocalDate(), "yyyy-MM-dd");

            List<InvoiceOperatingSplit> splitList = new ArrayList<>();
            // 汇总
            splitList.add(bankRecord(increment, priceList.get(0).toString(), firstInvoice.getBuyerName(), date));

            List<InvoiceOperatingSplit> taxList = new ArrayList<>();
            List<InvoiceOperatingSplit> incomeList = new ArrayList<>();
            // 汇总税
            for (InvoiceOperating invoiceOperating : invoiceOperatingList) {
                String itemName = getItemName(invoiceOperating);
                // 科目编号
                Map<String, String> nameValueMap = accountingSubjectService.findOperatingData(itemName);
                taxList.add(taxRecord(increment, invoiceOperating, itemName, nameValueMap.get("经营核算税科目映射关系")));
                incomeList.add(incomeRecord(increment, invoiceOperating, itemName, nameValueMap.get("经营核算收入科目映射关系")));
            }
            splitList.addAll(taxList);
            splitList.addAll(incomeList);
            splitList.forEach(split -> split.setName("开票分录"));
            saveSplit(splitList, taskId, period);
        }
        // 当前流水对账
        List<BankFlowLog> currentBankFlowLogList = bankFlowLogService.findByPeriod(period, CommonConstants.RECEIVABLE_OPERATE, "eq");
        // 往期流水对账
        List<BankFlowLog> pastBankFlowLogList = bankFlowLogService.findByPeriod(period, CommonConstants.RECEIVABLE_OPERATE, "lt");

        for (BankFlowLog bankFlowLog : currentBankFlowLogList) {
            log.info("处理当前流水对账的分录");
            int increment = atomicInteger.incrementAndGet();
            int i = bankFlowLog.getType().equals(CommonConstants.CANCEL_RECONCILIATION) ? -1 : 1;
            LocalDate date = getSplitDate(bankFlowLog);

            InvoiceOperatingSplit temp1 = recRecord(increment, date, bankFlowLog, bankFlowLog.getAmount() * i, 0d, "100203");
            InvoiceOperatingSplit temp2 = recRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount() * i, "12129902");
            InvoiceOperatingSplit temp3 = recRecord(increment, date, bankFlowLog, bankFlowLog.getAmount() * i, 0d, "800102");
            InvoiceOperatingSplit temp4 = recRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount() * i, "6401");
            temp2.setDivergenceCode("0");
            temp4.setDivergenceCode("0");
            temp4.setFuncClassificCode("2013850");
            temp4.setFundsCode("34");
            List<InvoiceOperatingSplit> splitList = List.of(temp1, temp2, temp3, temp4);
            splitList.forEach(split -> split.setName("回款对账分录"));
            saveSplit(splitList, taskId, period);
        }
        // 本期进行了对账的银行流水
        Set<String> bankFlowIds = currentBankFlowLogList.stream().map(BankFlowLog::getBankFlowId).collect(Collectors.toSet());
        List<BankFlow> currentBankFlow = bankFlowService.lambdaQuery().in(bankFlowIds.size() > 0, BankFlow::getId, bankFlowIds).list();

        for (BankFlow bankFlow : currentBankFlow) {
            // 只有剩余未对账金额时生成
            if (bankFlow.getUnConfirmPrice() > 0) {
                log.info("处理流水对账未完全的分录");
                int increment = atomicInteger.incrementAndGet();
                BankFlowLog bankFlowLog = currentBankFlowLogList.stream().filter(log -> log.getBankFlowId().equals(bankFlow.getId())).findAny().get();
                LocalDate date = getSplitDate(bankFlowLog);
                InvoiceOperatingSplit temp1 = recRecord(increment, date, bankFlowLog, bankFlow.getUnConfirmPrice(), 0d, "100203");
                InvoiceOperatingSplit temp2 = recRecord(increment, date, bankFlowLog, 0d, bankFlow.getUnConfirmPrice(), "2305010202");
                List<InvoiceOperatingSplit> splitList = List.of(temp1, temp2);
                splitList.forEach(split -> split.setName("回款预收分录"));
                saveSplit(splitList, taskId, period);
            }
        }

        // 本期没有对账的银行流水
        List<BankFlow> bankFlowList = bankFlowService.lambdaQuery().eq(BankFlow::getPeriod, period).eq(BankFlow::getSelfAccount, "2103215119300148266").eq(BankFlow::getReconciliationFlag, CommonConstants.NOT_RECONCILED)
                .notIn(bankFlowIds.size() > 0, BankFlow::getId, bankFlowIds).list();
        for (BankFlow bankFlow : bankFlowList) {
            log.info("处理流水没有对账的分录");
            int increment = atomicInteger.incrementAndGet();
            LocalDate date = LocalDate.now();
            BankFlowLog bankFlowLog = new BankFlowLog();
            bankFlowLog.setClientOrgName(bankFlow.getAdversaryOrgName());
            InvoiceOperatingSplit temp1 = recRecord(increment, date, bankFlowLog, bankFlow.getUnConfirmPrice(), 0d, "100203");
            InvoiceOperatingSplit temp2 = recRecord(increment, date, bankFlowLog, 0d, bankFlow.getUnConfirmPrice(), "2305010202");
            List<InvoiceOperatingSplit> splitList = List.of(temp1, temp2);
            splitList.forEach(split -> split.setName("回款预收分录"));
            saveSplit(splitList, taskId, period);
        }


        Map<String, List<BankFlowLog>> map = pastBankFlowLogList.stream().collect(Collectors.groupingBy(BankFlowLog::getBankFlowId));
        // 往期对账未取消对账
        List<BankFlowLog> pastBankUnCancelList = new ArrayList<>();
        for (String bankFlowId : map.keySet()) {
            List<BankFlowLog> bankFlowLogs = map.get(bankFlowId);
            boolean match = bankFlowLogs.stream().allMatch(bankFlowLog -> !bankFlowLog.getType().equals(CommonConstants.CANCEL_RECONCILIATION));
            if (match) {
                pastBankUnCancelList.addAll(bankFlowLogs);
            }
        }

        for (BankFlowLog bankFlowLog : pastBankUnCancelList) {
            log.info("处理预收冲应收的分录");
            int increment = atomicInteger.incrementAndGet();
            LocalDate date = getSplitDate(bankFlowLog);
            InvoiceOperatingSplit temp1 = recRecord(increment, date, bankFlowLog, bankFlowLog.getAmount(), 0d, "2305010202");
            InvoiceOperatingSplit temp2 = recRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount(), "12129902");
            InvoiceOperatingSplit temp3 = recRecord(increment, date, bankFlowLog, bankFlowLog.getAmount(), 0d, "800102");
            InvoiceOperatingSplit temp4 = recRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount(), "6401");
            temp2.setDivergenceCode("0");
            temp4.setDivergenceCode("0");
            temp4.setFuncClassificCode("2013850");
            temp4.setFundsCode("34");
            List<InvoiceOperatingSplit> splitList = List.of(temp1, temp2, temp3, temp4);
            splitList.forEach(split -> split.setName("预收冲应收分录"));
            saveSplit(splitList, taskId, period);
        }

        return atomicInteger.get();
    }

    private LocalDate getSplitDate(BankFlowLog bankFlowLog) {
        LocalDate date = null;
        // 对账日期，如果在本月就取对账日期，发生在次月，就取发票会计期的最后一天
        String receivablePeriod = bankFlowLog.getReceivablePeriod();
        if (receivablePeriod.equals(bankFlowLog.getPeriod())) {
            date = bankFlowLog.getCreateTime().toLocalDate();
        } else {
            Integer year = Integer.parseInt(receivablePeriod.substring(4));
            Integer month = Integer.parseInt(receivablePeriod.substring(4, 6));
            LocalDate localDate = LocalDate.of(year, month, 1);
            // 最后一天
            date = localDate.withDayOfMonth(localDate.lengthOfMonth());
        }
        return date;
    }

    private void saveSplit(List<InvoiceOperatingSplit> splitList, String taskId, String period) {
        for (InvoiceOperatingSplit s : splitList) {
            s.setTaskId(taskId);
            s.setPeriod(period);
        }
        invoiceOperatingSplitService.saveBatch(splitList);
    }

    private InvoiceOperatingSplit bankRecord(Integer id, String totalPrice, String buyerName, String invoiceDate) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setDate(invoiceDate);
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode("12129902");
        split.setSummary(buyerName);
        split.setBorrow(totalPrice);
        split.setLoan("0");
        split.setAttachmentNum("0");
        split.setUseFor(buyerName);
        split.setCreateTime(LocalDateTime.now());
        return split;
    }

    private InvoiceOperatingSplit incomeRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String subjectCode) {
        // 经营核算收入科目映射关系
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPriceExcludingTax());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setDivergenceCode("0");
        split.setFundsCode("34");
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    private InvoiceOperatingSplit taxRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String subjectCode) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalTaxPrice());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setDivergenceCode("0");
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    /**
     * 对账记录
     *
     * @return
     */
    private InvoiceOperatingSplit recRecord(Integer id, LocalDate invoiceDate, BankFlowLog bankFlowLog, Double borrow, Double loan, String subjectCode) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceDate, "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(bankFlowLog.getClientOrgName());
        split.setBorrow(borrow.toString());
        split.setLoan(loan.toString());
        split.setAttachmentNum("0");
        split.setUseFor(bankFlowLog.getClientOrgName());
        split.setDivergenceCode("0");
        split.setInvoiceOperatingId(bankFlowLog.getReceivableId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    public List<Double> computeTotalPrice(List<InvoiceOperating> invoiceOperatingList) {
        Double priceIncludingTax = 0d;
        Double priceExcludingTax = 0d;
        Double taxPrice = 0d;
        for (InvoiceOperating invoiceOperating : invoiceOperatingList) {
            priceIncludingTax += Double.parseDouble(invoiceOperating.getPriceIncludingTax());
            priceExcludingTax += Double.parseDouble(invoiceOperating.getPriceExcludingTax());
            taxPrice += Double.parseDouble(invoiceOperating.getTaxPrice());
        }

        return List.of(priceIncludingTax, priceExcludingTax, taxPrice);
    }

    private String getItemName(InvoiceOperating invoice) {
        String productName = invoice.getProductName();
        String[] productNameSplit = productName.split("\\*");
        String itemName = productNameSplit[productNameSplit.length - 1];
        return itemName;
    }

}
