package com.ocs.busi.task.handle;

import cn.hutool.core.util.IdUtil;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.domain.entity.InvoiceFinanceSplit;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.busi.service.InvoiceFinanceSplitService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Component
public class InvoiceFinanceHandle {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceFinanceHandle.class);

    @Autowired
    private AccountingSubjectService accountingSubjectService;
    @Autowired
    private InvoiceFinanceSplitService invoiceFinanceSplitService;
    @Autowired
    private InvoiceFinanceService invoiceFinanceService;
    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 发票拆分
     *
     * @param startCertificateId 起始凭照号ID
     * @param invoiceFinanceList 发票数据
     * @param accountingPeriod   会计期间
     * @param taskId             任务ID
     * @return
     */
    @Transactional
    public Integer dataSplit(Integer startCertificateId, List<InvoiceFinance> invoiceFinanceList, String accountingPeriod, String taskId) {
        AtomicInteger atomicInteger;
        if (startCertificateId == null) {
            SysDictData dictData = new SysDictData();
            dictData.setDictType("invoice_finance_period");
            List<SysDictData> sysDictDataList = dictDataService.selectDictDataList(dictData);

            Optional<String> periodOptional = sysDictDataList.stream().filter(data -> data.getDictLabel().equals(accountingPeriod)).map(data -> data.getDictValue()).findFirst();
            if (!periodOptional.isPresent()) {
                throw new ServiceException("请配置会计期间,期间值" + accountingPeriod + ",不存在");
            }
            atomicInteger = new AtomicInteger(Integer.parseInt(periodOptional.get()));
        } else {
            atomicInteger = new AtomicInteger(startCertificateId);
        }

        Integer periodNumMax = invoiceFinanceSplitService.findPeriodNumMax(accountingPeriod);
        final Integer periodNum = periodNumMax == null ? 1 : periodNumMax + 1;

        for (InvoiceFinance invoiceFinance : invoiceFinanceList) {
            int increment = atomicInteger.incrementAndGet();
            String itemName = invoiceFinance.getItemName();

            AccountingSubject financeItemValue = accountingSubjectService.findFinanceItemValue(itemName);

            if (financeItemValue == null) {
                logger.error("未找到科目编码");
            }

            InvoiceFinanceSplit bankRecordSplit = bankRecord(increment, invoiceFinance, taskId);
            InvoiceFinanceSplit financeRecordSplit = financeRecord(increment, invoiceFinance, taskId, financeItemValue.getValue());

            invoiceFinance.setDataSplit(true);
            // CZFPENTRY202211V001
            invoiceFinance.setVersion("CZFPENTRY" + accountingPeriod + StringUtils.leftPad(periodNum + "", 3, "0"));

            logger.info("财政发票:{},开始保存分录拆分数据", invoiceFinance.getId());
            List<InvoiceFinanceSplit> invoiceFinanceSplitList = Arrays.asList(bankRecordSplit, financeRecordSplit);
            invoiceFinanceSplitList.forEach(split -> {
                split.setAccountingPeriod(accountingPeriod);
                split.setPeriodNum(periodNum);
                invoiceFinanceSplitService.save(split);
            });

            logger.info("财政发票:{},更新经营性发票数据", invoiceFinance.getId());
            invoiceFinanceService.updateById(invoiceFinance);
        }
        return atomicInteger.get();
    }


    public InvoiceFinanceSplit bankRecord(Integer id, InvoiceFinance invoiceFinance, String taskId) {
        InvoiceFinanceSplit split = new InvoiceFinanceSplit();
        split.setId(IdUtil.objectId());
        split.setDate(invoiceFinance.getInvoicingDate());
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode("100204");
        split.setSummary(invoiceFinance.getPayer());
        split.setBorrow(invoiceFinance.getPrice());
        split.setLoan("0");
        split.setAttachmentNum("0");
        split.setUseFor(invoiceFinance.getPayer());
        split.setTaskId(taskId);
        split.setInvoiceFinanceId(invoiceFinance.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

    public InvoiceFinanceSplit financeRecord(Integer id, InvoiceFinance invoiceFinance, String taskId, String subjectCode) {
        InvoiceFinanceSplit split = new InvoiceFinanceSplit();
        split.setId(IdUtil.objectId());
        split.setDate(invoiceFinance.getInvoicingDate());
        split.setCertificateType("记账");
        split.setCertificateId(id);
        split.setSubjectCode(subjectCode);
        split.setSummary(invoiceFinance.getPayer());
        split.setBorrow("0");
        split.setLoan(invoiceFinance.getPrice());
        split.setAttachmentNum("0");
        split.setUseFor(invoiceFinance.getPayer());
        split.setTaskId(taskId);
        split.setInvoiceFinanceId(invoiceFinance.getId());
        split.setCreateTime(LocalDateTime.now());

        return split;
    }

}
