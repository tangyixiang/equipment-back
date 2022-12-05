package com.ocs.web.controller.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.dto.TaskDto;
import com.ocs.busi.domain.dto.TaskRunDto;
import com.ocs.busi.domain.entity.InvoiceDataSplit;
import com.ocs.busi.domain.entity.InvoiceFinanceSplit;
import com.ocs.busi.domain.entity.InvoiceOperatingSplit;
import com.ocs.busi.service.InvoiceFinanceSplitService;
import com.ocs.busi.service.InvoiceOperatingSplitService;
import com.ocs.common.constant.TaskIDPrefixConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.utils.StringUtils;
import com.ocs.common.utils.poi.ExcelUtil;
import com.ocs.quartz.domain.SysJob;
import com.ocs.quartz.domain.SysJobLog;
import com.ocs.quartz.service.ISysJobLogService;
import com.ocs.quartz.service.ISysJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author tangyixiang
 * @Date 2022/11/10
 */
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private ISysJobLogService sysJobLogService;
    @Autowired
    private ISysJobService sysJobService;
    @Autowired
    private InvoiceFinanceSplitService invoiceFinanceSplitService;
    @Autowired
    private InvoiceOperatingSplitService invoiceOperatingSplitService;


    @GetMapping("/log/list")
    public TableDataInfo list(TaskDto taskDto) {
        startPage("create_time desc");
        SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(taskDto.getTaskName());
        sysJobLog.setStatus(taskDto.getTaskStatus());
        sysJobLog.setStartTime(taskDto.getStartDate());
        sysJobLog.setStopTime(taskDto.getEndDate());

        List<SysJobLog> sysJobLogList = sysJobLogService.selectJobLogList(sysJobLog);
        return getDataTable(sysJobLogList);
    }

    @PostMapping("/split/result")
    public void export(HttpServletResponse response, Long jobLogId) {
        SysJobLog sysJobLog = sysJobLogService.selectJobLogById(jobLogId);
        String taskId = sysJobLog.getTaskId();
        if (StringUtils.isNotEmpty(taskId)) {
            LambdaQueryWrapper<InvoiceOperatingSplit> wrapper1 = new LambdaQueryWrapper<InvoiceOperatingSplit>().eq(InvoiceOperatingSplit::getTaskId, TaskIDPrefixConstants.OPERATE_TASK + taskId);
            List<InvoiceOperatingSplit> invoiceOperatingSplits = invoiceOperatingSplitService.list(wrapper1);

            LambdaQueryWrapper<InvoiceFinanceSplit> wrapper2 = new LambdaQueryWrapper<InvoiceFinanceSplit>().eq(InvoiceFinanceSplit::getTaskId, TaskIDPrefixConstants.FINANCE_TASK + taskId);
            List<InvoiceFinanceSplit> invoiceFinanceSplits = invoiceFinanceSplitService.list(wrapper2);

            List<InvoiceDataSplit> list = new ArrayList<>();
            invoiceOperatingSplits.forEach(op -> {
                InvoiceDataSplit data = new InvoiceDataSplit();
                BeanUtils.copyProperties(op, data);
                data.setSplitType("经营类");
                list.add(data);
            });
            invoiceFinanceSplits.forEach(finance -> {
                InvoiceDataSplit data = new InvoiceDataSplit();
                BeanUtils.copyProperties(finance, data);
                data.setSplitType("财政类");
                list.add(data);
            });

            ExcelUtil<InvoiceDataSplit> util = new ExcelUtil<>(InvoiceDataSplit.class);
            util.exportExcel(response, list, "分录数据");
        } else {
            throw new ServiceException("错误的jobLogId");
        }
    }

    @PostMapping("/run")
    public Result run(@RequestBody @Validated TaskRunDto taskRunDto) {
        SysJob sysJob = sysJobService.selectJobById(taskRunDto.getJobId());
        sysJob.setParam(taskRunDto.getParams());
        CompletableFuture.runAsync(() -> {
            try {
                sysJobService.updateJob(sysJob);
                sysJobService.run(sysJob);
            } catch (Exception e) {
                logger.error("作业中心运行失败,错误原因:{}", e);
            }
        });
        return Result.success();
    }


}
