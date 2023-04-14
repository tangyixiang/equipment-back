package com.ocs.busi.task.handle;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ocs.busi.domain.entity.*;
import com.ocs.busi.helper.InvoiceHelper;
import com.ocs.busi.service.*;
import com.ocs.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class InvoiceFinanceHandle {

    @Autowired
    private AccountingSubjectService accountingSubjectService;
    @Autowired
    private InvoiceFinanceSplitService invoiceFinanceSplitService;
    @Autowired
    private InvoiceFinanceService invoiceFinanceService;
    @Autowired
    private InvoiceHelper invoiceHelper;
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
        AtomicInteger atomicInteger = new AtomicInteger(startCertificateId);
        Map<String, List<CompanyReceivables>> collect = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getInvoiceId));
        // 开票
        for (String invoiceId : collect.keySet()) {
            log.info("开始处理发票ID:{}", invoiceId);
            List<CompanyReceivables> list = collect.get(invoiceId);
            List<String> idList = list.stream().map(CompanyReceivables::getId).collect(Collectors.toList());
            List<InvoiceFinance> invoiceFinanceList = invoiceFinanceService.listByIds(idList);
            List<InvoiceFinanceSplit> splitList = new ArrayList<>();

            int increment = atomicInteger.incrementAndGet();
            // 开票日期
            InvoiceFinance fristInvoice = invoiceFinanceList.get(0);
            // 发票时间
            LocalDate invoiceDate = LocalDate.parse(fristInvoice.getInvoicingDate());
            // 会计最后一天
            LocalDate periodDate = invoiceHelper.getPeriodLastDate(fristInvoice.getInvoicingPeriod());
            LocalDate actualDate = invoiceDate.isBefore(periodDate) ? invoiceDate : periodDate;
            String date = LocalDateTimeUtil.format(actualDate, "yyyy-MM-dd");

            // 汇总
            double totalPrice = invoiceFinanceList.stream().mapToDouble(finance -> Double.parseDouble(finance.getPrice())).sum();
            splitList.add(bankRecord(increment, date, fristInvoice, totalPrice + ""));

            for (InvoiceFinance invoiceFinance : invoiceFinanceList) {
                String itemName = invoiceFinance.getItemName();
                AccountingSubject financeItemValue = accountingSubjectService.findFinanceItemValue(itemName);
                if (financeItemValue == null) {
                    log.error("未找到科目编码,科目名称:{}", itemName);
                }
                splitList.add(financeRecord(increment, date, invoiceFinance, financeItemValue == null ? "" : financeItemValue.getValue()));
            }
            splitList.forEach(split -> split.setName("开票分录"));
            saveSplit(splitList, taskId, period);
        }

        // 当前流水对账
        List<BankFlowLog> currentBankFlowLogList = bankFlowLogService.findByPeriod(period, CommonConstants.RECEIVABLE_FINANCE, "eq");
        // 往期流水对账
        List<BankFlowLog> pastBankFlowLogList = bankFlowLogService.findByPeriod(period, CommonConstants.RECEIVABLE_FINANCE, "lt");

        for (BankFlowLog bankFlowLog : currentBankFlowLogList) {
            log.info("处理当前流水对账分录");
            int increment = atomicInteger.incrementAndGet();
            int i = bankFlowLog.getType().equals(CommonConstants.CANCEL_RECONCILIATION) ? -1 : 1;
            LocalDate periodLastDate = invoiceHelper.getPeriodLastDate(bankFlowLog.getPeriod());
            LocalDate date = bankFlowLog.getCreateTime().toLocalDate().isBefore(periodLastDate) ? bankFlowLog.getCreateTime().toLocalDate() : periodLastDate;
            InvoiceFinanceSplit temp1 = bankFlowRecord(increment, date, bankFlowLog, bankFlowLog.getAmount() * i, 0d, "100204");
            InvoiceFinanceSplit temp2 = bankFlowRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount() * i, "12129901");
            List<InvoiceFinanceSplit> splitList = List.of(temp1, temp2);
            splitList.forEach(split -> split.setName("回款对账分录"));
            saveSplit(splitList, taskId, period);
        }

        // 本期进行了对账的银行流水
        Set<String> bankFlowIds = currentBankFlowLogList.stream().map(BankFlowLog::getBankFlowId).collect(Collectors.toSet());
        if (bankFlowIds.size() > 0) {
            List<BankFlow> currentBankFlow = bankFlowService.listByIds(bankFlowIds);
            for (BankFlow bankFlow : currentBankFlow) {
                // 只有剩余未对账金额时生成
                if (bankFlow.getUnConfirmPrice() > 0) {
                    log.info("处理流水对账未完全的分录");
                    int increment = atomicInteger.incrementAndGet();
                    BankFlowLog bankFlowLog = currentBankFlowLogList.stream().filter(log -> log.getBankFlowId().equals(bankFlow.getId())).findAny().get();
                    LocalDate date = invoiceHelper.getPeriodLastDate(bankFlow.getPeriod());
                    InvoiceFinanceSplit temp1 = bankFlowRecord(increment, date, bankFlowLog, bankFlow.getUnConfirmPrice(), 0d, "100204");
                    InvoiceFinanceSplit temp2 = bankFlowRecord(increment, date, bankFlowLog, 0d, bankFlow.getUnConfirmPrice(), "21030118");
                    List<InvoiceFinanceSplit> splitList = List.of(temp1, temp2);
                    splitList.forEach(split -> split.setName("回款预收分录"));
                    saveSplit(splitList, taskId, period);
                }
            }
        }

        // 本期没有对账的银行流水
        List<BankFlow> bankFlowList = bankFlowService.lambdaQuery().eq(BankFlow::getPeriod, period).eq(BankFlow::getSelfAccount, "9558852102002052299").eq(BankFlow::getReconciliationFlag, CommonConstants.NOT_RECONCILED)
                .notIn(bankFlowIds.size() > 0, BankFlow::getId, bankFlowIds).list();
        for (BankFlow bankFlow : bankFlowList) {
            // 只有剩余未对账金额时生成
            log.info("处理流水没有对账分录");
            int increment = atomicInteger.incrementAndGet();
            LocalDate date = invoiceHelper.getPeriodLastDate(bankFlow.getPeriod());
            BankFlowLog bankFlowLog = new BankFlowLog();
            bankFlowLog.setClientOrgName(bankFlow.getAdversaryOrgName());
            InvoiceFinanceSplit temp1 = bankFlowRecord(increment, date, bankFlowLog, bankFlow.getUnConfirmPrice(), 0d, "100204");
            InvoiceFinanceSplit temp2 = bankFlowRecord(increment, date, bankFlowLog, 0d, bankFlow.getUnConfirmPrice(), "21030118");
            List<InvoiceFinanceSplit> splitList = List.of(temp1, temp2);
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
            int increment = atomicInteger.incrementAndGet();
            LocalDate periodLastDate = invoiceHelper.getPeriodLastDate(bankFlowLog.getPeriod());
            LocalDate date = bankFlowLog.getCreateTime().toLocalDate().isBefore(periodLastDate) ? bankFlowLog.getCreateTime().toLocalDate() : periodLastDate;

            InvoiceFinanceSplit temp1 = bankFlowRecord(increment, date, bankFlowLog, bankFlowLog.getAmount(), 0d, "21030118");
            InvoiceFinanceSplit temp2 = bankFlowRecord(increment, date, bankFlowLog, 0d, bankFlowLog.getAmount(), "12129901");
            List<InvoiceFinanceSplit> splitList = List.of(temp1, temp2);
            splitList.forEach(split -> split.setName("预收冲应收分录"));
            saveSplit(splitList, taskId, period);
        }

        return atomicInteger.get();
    }

    private void saveSplit(List<InvoiceFinanceSplit> splitList, String taskId, String period) {
        for (InvoiceFinanceSplit s : splitList) {
            s.setTaskId(taskId);
            s.setPeriod(period);
        }
        invoiceFinanceSplitService.saveBatch(splitList);
    }

    public InvoiceFinanceSplit bankRecord(Integer id, String date, InvoiceFinance invoiceFinance, String totalPrice) {
        InvoiceFinanceSplit split = new InvoiceFinanceSplit();
        split.setId(IdUtil.objectId());
        split.setDate(date);
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode("12129901");
        split.setSummary(invoiceFinance.getPayer());
        split.setBorrow(totalPrice);
        split.setLoan("0");
        split.setAttachmentNum("0");
        split.setUseFor(invoiceFinance.getPayer());
        split.setInvoiceFinanceId(invoiceFinance.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    public InvoiceFinanceSplit financeRecord(Integer id, String date, InvoiceFinance invoiceFinance, String subjectCode) {
        InvoiceFinanceSplit split = new InvoiceFinanceSplit();
        split.setId(IdUtil.objectId());
        split.setDate(date);
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceFinance.getPayer());
        split.setBorrow("0");
        split.setLoan(invoiceFinance.getPrice());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceFinance.getPayer());
        split.setInvoiceFinanceId(invoiceFinance.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    public InvoiceFinanceSplit bankFlowRecord(Integer id, LocalDate date, BankFlowLog bankFlowLog, Double borrow, Double Loan, String subjectCode) {
        InvoiceFinanceSplit split = new InvoiceFinanceSplit();
        split.setId(IdUtil.objectId());
        split.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(bankFlowLog.getClientOrgName());
        split.setBorrow(borrow.toString());
        split.setLoan(Loan.toString());
        split.setAttachmentNum("0");
        split.setUseFor(bankFlowLog.getClientOrgName());
        split.setInvoiceFinanceId(bankFlowLog.getReceivableId());
        split.setCreateTime(LocalDateTime.now());

        return split;
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

}
