package com.ocs.quartz.util;

import cn.hutool.core.util.IdUtil;
import com.ocs.common.constant.Constants;
import com.ocs.common.constant.ScheduleConstants;
import com.ocs.common.task.SysJobRuntime;
import com.ocs.common.task.TaskContext;
import com.ocs.common.utils.ExceptionUtil;
import com.ocs.common.utils.StringUtils;
import com.ocs.common.utils.bean.BeanUtils;
import com.ocs.common.utils.spring.SpringUtils;
import com.ocs.quartz.domain.SysJob;
import com.ocs.quartz.domain.SysJobLog;
import com.ocs.quartz.service.ISysJobLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 抽象quartz调用
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJob sysJob = new SysJob();
        BeanUtils.copyBeanProp(sysJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));
        try {
            before(context, sysJob);
            if (sysJob != null) {
                doExecute(context, sysJob);
            }
            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        SysJobRuntime runtime = new SysJobRuntime();
        runtime.setStartTime(LocalDateTime.now());
        runtime.setTaskId(IdUtil.objectId());
        TaskContext.set(runtime);
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        SysJobRuntime runtime = TaskContext.get();
        TaskContext.clear();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setStartTime(runtime.getStartTime());
        sysJobLog.setTaskId(runtime.getTaskId());
        sysJobLog.setTaskType(runtime.getTaskType());
        sysJobLog.setStopTime(LocalDateTime.now());

        long runMs = Duration.between(sysJobLog.getStartTime(), sysJobLog.getStopTime()).toMillis();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            sysJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库当中
        SpringUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
