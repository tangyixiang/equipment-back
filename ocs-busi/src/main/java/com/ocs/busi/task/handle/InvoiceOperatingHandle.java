package com.ocs.busi.task.handle;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ocs.busi.domain.entity.*;
import com.ocs.busi.service.*;
import com.ocs.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private CompanyReceivablesService receivablesService;

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

        // 会计期间运行次数
        Integer periodNumMax = invoiceOperatingSplitService.findPeriodNumMax(period);
        final Integer periodNum = periodNumMax == null ? 1 : periodNumMax + 1;

        Map<String, List<CompanyReceivables>> collect = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getInvoiceId));

        for (String invoiceId : collect.keySet()) {
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
            saveSplit(splitList, taskId, period);
        }

        List<BankFlowLog> bankFlowLogList = bankFlowLogService.lambdaQuery()
                .eq(BankFlowLog::getPeriod, period).eq(BankFlowLog::getReceivableType, CommonConstants.RECEIVABLE_OPERATE).list();

        for (BankFlowLog bankFlowLog : bankFlowLogList) {
            int increment = atomicInteger.incrementAndGet();
            List<InvoiceOperatingSplit> splitList = new ArrayList<>();

            int i = bankFlowLog.getType().equals(CommonConstants.CANCEL_RECONCILIATION) ? -1 : 1;

            InvoiceOperatingSplit temp1 = recRecord(increment, bankFlowLog.getInvoiceDate(), bankFlowLog.getClientOrgName(), bankFlowLog.getAmount() * i, 0d, "100203", bankFlowLog.getReceivableId());
            InvoiceOperatingSplit temp2 = recRecord(increment, bankFlowLog.getInvoiceDate(), bankFlowLog.getClientOrgName(), 0d, bankFlowLog.getAmount() * i, "12129902", bankFlowLog.getReceivableId());
            InvoiceOperatingSplit temp3 = recRecord(increment, bankFlowLog.getInvoiceDate(), bankFlowLog.getClientOrgName(), bankFlowLog.getAmount() * i, 0d, "800102", bankFlowLog.getReceivableId());
            InvoiceOperatingSplit temp4 = recRecord(increment, bankFlowLog.getInvoiceDate(), bankFlowLog.getClientOrgName(), 0d, bankFlowLog.getAmount() * i, "6401", bankFlowLog.getReceivableId());

            temp2.setDivergenceCode("0");
            temp4.setDivergenceCode("0");
            temp4.setFuncClassificCode("2013850");
            temp4.setFundsCode("34");
            splitList.addAll(List.of(temp1, temp2, temp3, temp4));

            saveSplit(splitList, taskId, period);
        }


        //TODO 对账前，先计算出之前的结余，然后再计算本期的对账
        List<BankFlow> bankFlowList = bankFlowService.countPriceBeforePeriod(period);
        if (ObjectUtils.isNotEmpty(bankFlowList)) {

        }

        //


        return atomicInteger.get();
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
    private InvoiceOperatingSplit recRecord(Integer id, LocalDate invoiceDate, String buyerName, Double borrow, Double loan, String subjectCode, String operatingId) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceDate, "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(buyerName);
        split.setBorrow(borrow.toString());
        split.setLoan(loan.toString());
        split.setAttachmentNum("0");
        split.setUseFor(buyerName);
        split.setDivergenceCode("0");
        split.setInvoiceOperatingId(operatingId);
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
