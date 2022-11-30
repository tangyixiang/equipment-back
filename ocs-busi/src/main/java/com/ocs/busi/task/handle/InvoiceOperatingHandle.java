package com.ocs.busi.task.handle;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.domain.entity.InvoiceOperating;
import com.ocs.busi.domain.entity.InvoiceOperatingSplit;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.busi.service.InvoiceOperatingService;
import com.ocs.busi.service.InvoiceOperatingSplitService;
import com.ocs.common.core.domain.entity.SysDictData;
import com.ocs.common.exception.ServiceException;
import com.ocs.system.service.ISysDictDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Component
public class InvoiceOperatingHandle {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceOperatingHandle.class);

    @Autowired
    private AccountingSubjectService accountingSubjectService;
    @Autowired
    private InvoiceOperatingSplitService invoiceOperatingSplitService;
    @Autowired
    private InvoiceOperatingService invoiceOperatingService;
    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 发票拆分
     *
     * @param startCertificateId 起始凭照号ID
     * @param invoiceOperatings  发票数据
     * @param accountingPeriod   会计期间
     * @param taskId             任务ID
     * @return
     */
    @Transactional
    public Integer dataSplit(Integer startCertificateId, List<InvoiceOperating> invoiceOperatings, String accountingPeriod, String taskId) {
        // 初始化凭照号码起始值
        AtomicInteger atomicInteger;
        if (startCertificateId == null) {
            SysDictData dictData = new SysDictData();
            dictData.setDictType("invoice_operating_period");
            List<SysDictData> sysDictDataList = dictDataService.selectDictDataList(dictData);

            Optional<String> periodOptional = sysDictDataList.stream().filter(data -> data.getDictLabel().equals(accountingPeriod)).map(data -> data.getDictValue()).findFirst();
            if (!periodOptional.isPresent()) {
                throw new ServiceException("请配置会计期间,期间值" + accountingPeriod + ",不存在");
            }
            atomicInteger = new AtomicInteger(Integer.parseInt(periodOptional.get()));
        } else {
            atomicInteger = new AtomicInteger(startCertificateId);
        }
        // 会计期间运行次数
        Integer periodNumMax = invoiceOperatingSplitService.findPeriodNumMax(accountingPeriod);
        final Integer periodNum = periodNumMax == null ? 1 : periodNumMax + 1;

        for (InvoiceOperating invoiceOperating : invoiceOperatings) {
            int increment = atomicInteger.incrementAndGet();
            String productName = invoiceOperating.getProductName();
            String[] productNameSplit = productName.split("\\*");
            String itemName = productNameSplit[productNameSplit.length - 1];
            // 科目编号
            Map<String, String> nameValueMap = accountingSubjectService.findOperatingData(itemName);

            InvoiceOperatingSplit bankRecordSplit = bankRecord(increment, invoiceOperating, itemName, taskId);
            InvoiceOperatingSplit fundsRecordSplit = fundsRecord(increment, invoiceOperating, itemName, taskId);
            InvoiceOperatingSplit incomeRecordSplit = incomeRecord(increment, invoiceOperating, itemName, taskId, nameValueMap.get("经营核算收入科目映射关系"));
            InvoiceOperatingSplit taxRecordSplit = taxRecord(increment, invoiceOperating, itemName, taskId, nameValueMap.get("经营核算税科目映射关系"));
            InvoiceOperatingSplit budgetRecordSplit = budgetRecord(increment, invoiceOperating, itemName, taskId, nameValueMap.get("经营预算收入科目映射关系"));

            invoiceOperating.setDataSplit(true);
            //JYFPENTRY202211V001
            invoiceOperating.setVersion("JYFPENTRY" + accountingPeriod + StringUtils.leftPad(periodNum + "", 3, "0"));

            logger.info("经营发票:{},开始保存分录拆分数据", invoiceOperating.getId());
            List<InvoiceOperatingSplit> invoiceOperatingSplitList = Arrays.asList(bankRecordSplit, fundsRecordSplit, incomeRecordSplit, taxRecordSplit, budgetRecordSplit);
            invoiceOperatingSplitList.forEach(split -> {
                split.setAccountingPeriod(accountingPeriod);
                split.setPeriodNum(periodNum);
                invoiceOperatingSplitService.save(split);
            });


            logger.info("经营发票:{},更新经营性发票数据", invoiceOperating.getId());
            invoiceOperatingService.updateById(invoiceOperating);
        }
        return atomicInteger.get();
    }

    private InvoiceOperatingSplit bankRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String taskId) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode("100203");
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPriceIncludingTax());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setBudgetProjectCode("");
        split.setFuncClassificCode("");
        split.setDeptEconomyCode("");
        split.setContactsCode("");
        split.setDivergenceCode("");
        split.setFundsCode("");
        split.setTaskId(taskId);
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    private InvoiceOperatingSplit fundsRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String taskId) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode("800102");
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPriceIncludingTax());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setBudgetProjectCode("");
        split.setFuncClassificCode("");
        split.setDeptEconomyCode("");
        split.setContactsCode("");
        split.setDivergenceCode("");
        split.setFundsCode("");
        split.setTaskId(taskId);
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    private InvoiceOperatingSplit incomeRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String taskId, String subjectCode) {
        // 经营核算收入科目映射关系

        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPriceIncludingTax());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setBudgetProjectCode("");
        split.setFuncClassificCode("");
        split.setDeptEconomyCode("");
        split.setContactsCode("");
        split.setDivergenceCode("0");
        split.setFundsCode("34");
        split.setTaskId(taskId);
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    private InvoiceOperatingSplit taxRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String taskId, String subjectCode) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPrice());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setBudgetProjectCode("");
        split.setFuncClassificCode("");
        split.setDeptEconomyCode("");
        split.setContactsCode("");
        split.setDivergenceCode("0");
        split.setFundsCode("34");
        split.setTaskId(taskId);
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    private InvoiceOperatingSplit budgetRecord(Integer id, InvoiceOperating invoiceOperating, String itemName, String taskId, String subjectCode) {
        InvoiceOperatingSplit split = new InvoiceOperatingSplit();
        split.setId(IdUtil.objectId());
        split.setDate(LocalDateTimeUtil.format(invoiceOperating.getInvoicingTime().toLocalDate(), "yyyy-MM-dd"));
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceOperating.getBuyerName() + " " + itemName);
        split.setBorrow("0");
        split.setLoan(invoiceOperating.getTotalPriceIncludingTax());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceOperating.getBuyerName());
        split.setBudgetProjectCode("");
        split.setFuncClassificCode("2013850");
        split.setDeptEconomyCode("");
        split.setContactsCode("");
        split.setDivergenceCode("0");
        split.setFundsCode("34");
        split.setTaskId(taskId);
        split.setInvoiceOperatingId(invoiceOperating.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

}
