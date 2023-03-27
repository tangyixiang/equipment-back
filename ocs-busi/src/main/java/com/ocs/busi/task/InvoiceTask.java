package com.ocs.busi.task;

import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.busi.service.FinancePeriodService;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.busi.task.handle.InvoiceFinanceHandle;
import com.ocs.busi.task.handle.InvoiceOperatingHandle;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.constant.TaskIDPrefixConstants;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.task.SysJobRuntime;
import com.ocs.common.task.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Slf4j
@Component
public class InvoiceTask {

    @Autowired
    private InvoiceOperatingHandle invoiceOperatingHandle;
    @Autowired
    private InvoiceFinanceService invoiceFinanceService;
    @Autowired
    private InvoiceFinanceHandle invoiceFinanceHandle;
    @Autowired
    private FinancePeriodService financePeriodService;
    @Autowired
    private CompanyReceivablesService receivablesService;

    /**
     * 分录
     *
     * @param periodId 会计期间ID
     */
    @Transactional
    public void splitTask(Integer periodId) {
        TaskContext.get().setTaskType(CommonConstants.TASK_SPLIT);
        log.info("发票分录任务开始");
        FinancePeriod financePeriod = financePeriodService.getById(periodId);
        if (financePeriod == null) {
            throw new ServiceException("会计期间不存在");
        }
        Integer certificateId = splitOperatingInvoice(financePeriod.getPeriod(), Integer.parseInt(financePeriod.getValue()));
        splitFinanceInvoice(financePeriod.getPeriod() + "", certificateId);
        log.info("发票分录任务完成");
    }


    /**
     * 经营性发票-分录
     *
     * @param period 会计日期
     */
    public Integer splitOperatingInvoice(String period, Integer certificateId) {
        SysJobRuntime runtime = TaskContext.get();
        String taskId = TaskIDPrefixConstants.OPERATE_TASK + runtime.getTaskId();
        log.info("经营性发票,分录任务开始执行,分配的任务ID:{}", taskId);

        List<CompanyReceivables> operateReceivableList = receivablesService.lambdaQuery()
                .in(CompanyReceivables::getSourceType, List.of(CommonConstants.RECEIVABLE_OPERATE, CommonConstants.RECEIVABLE_CUSTOM_FINANCE))
                .eq(CompanyReceivables::getPeriod, period)
                .orderByAsc(CompanyReceivables::getInvoicingDate).list();


        return invoiceOperatingHandle.dataSplit(certificateId, operateReceivableList, period, taskId);
    }

    /**
     * 经营性发票-分录
     *
     * @param period 会计日期
     */
    public Integer splitFinanceInvoice(String period, Integer certificateId) {
        SysJobRuntime runtime = TaskContext.get();
        String taskId = TaskIDPrefixConstants.FINANCE_TASK + runtime.getTaskId();
        log.info("财政性发票,分录任务开始执行,分配的任务ID:{}", taskId);

        List<CompanyReceivables> financeReceivableList = receivablesService.lambdaQuery()
                .in(CompanyReceivables::getSourceType, List.of(CommonConstants.RECEIVABLE_FINANCE, CommonConstants.RECEIVABLE_CUSTOM_OPERATE))
                .eq(CompanyReceivables::getPeriod, period)
                .orderByAsc(CompanyReceivables::getInvoicingDate).list();

        return invoiceFinanceHandle.dataSplit(certificateId, financeReceivableList, period, taskId);
    }

}
