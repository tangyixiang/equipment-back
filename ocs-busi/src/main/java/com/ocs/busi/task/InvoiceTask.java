package com.ocs.busi.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.domain.entity.InvoiceOperating;
import com.ocs.busi.service.FinancePeriodService;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.busi.service.InvoiceOperatingService;
import com.ocs.busi.task.handle.InvoiceFinanceHandle;
import com.ocs.busi.task.handle.InvoiceOperatingHandle;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.constant.TaskIDPrefixConstants;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.task.SysJobRuntime;
import com.ocs.common.task.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Component
public class InvoiceTask {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceTask.class);

    @Autowired
    private InvoiceOperatingService invoiceOperatingService;
    @Autowired
    private InvoiceOperatingHandle invoiceOperatingHandle;
    @Autowired
    private InvoiceFinanceService invoiceFinanceService;
    @Autowired
    private InvoiceFinanceHandle invoiceFinanceHandle;
    @Autowired
    private FinancePeriodService financePeriodService;

    /**
     * 分录
     *
     * @param periodId 会计期间ID
     */
    @Transactional
    public void splitTask(Integer periodId) {
        TaskContext.get().setTaskType(CommonConstants.TASK_SPLIT);
        logger.info("发票分录任务开始");
        FinancePeriod financePeriod = financePeriodService.getById(periodId);
        if (financePeriod == null) {
            throw new ServiceException("会计期间不存在");
        }
        Integer certificateId = splitOperatingInvoice(financePeriod.getPeriod(), Integer.parseInt(financePeriod.getValue()));
        splitFinanceInvoice(financePeriod.getPeriod() + "", certificateId);
        logger.info("发票分录任务完成");
    }


    /**
     * 经营性发票-分录
     *
     * @param accountingPeriod 会计日期
     */
    public Integer splitOperatingInvoice(String accountingPeriod, Integer certificateId) {
        SysJobRuntime runtime = TaskContext.get();
        String taskId = TaskIDPrefixConstants.OPERATE_TASK + runtime.getTaskId();
        logger.info("经营性发票,分录任务开始执行,分配的任务ID:{}", taskId);
        LambdaQueryWrapper<InvoiceOperating> wrapper = new LambdaQueryWrapper<InvoiceOperating>().eq(InvoiceOperating::getDataSplit, false);
        List<InvoiceOperating> list = invoiceOperatingService.list(wrapper);

        return invoiceOperatingHandle.dataSplit(certificateId, list, accountingPeriod, taskId);
    }

    /**
     * 经营性发票-分录
     *
     * @param accountingPeriod 会计日期
     */
    public Integer splitFinanceInvoice(String accountingPeriod, Integer certificateId) {
        SysJobRuntime runtime = TaskContext.get();
        String taskId = TaskIDPrefixConstants.FINANCE_TASK + runtime.getTaskId();
        logger.info("财政性发票,分录任务开始执行,分配的任务ID:{}", taskId);
        LambdaQueryWrapper<InvoiceFinance> wrapper = new LambdaQueryWrapper<InvoiceFinance>().eq(InvoiceFinance::getDataSplit, false);
        List<InvoiceFinance> list = invoiceFinanceService.list(wrapper);

        return invoiceFinanceHandle.dataSplit(certificateId, list, accountingPeriod, taskId);
    }

}
