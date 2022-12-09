package com.ocs.web.controller.report;

import cn.hutool.core.date.DateUtil;
import com.ocs.busi.domain.dto.EmployeeSalaryReportDto;
import com.ocs.busi.domain.vo.EmployeeSalaryReportVo;
import com.ocs.busi.report.EmployeeSalaryReportService;
import com.ocs.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private EmployeeSalaryReportService employeeSalaryReportService;

    @PostMapping("/employee/salary")
    public Result employeeSalary(@RequestBody EmployeeSalaryReportDto employeeSalaryReportDto) {
        List<LocalDateTime> range = employeeSalaryReportDto.getRange();
        List<String> condition = employeeSalaryReportDto.getCondition();

        employeeSalaryReportDto.setStartPeriod(DateUtil.format(range.get(0),"yyyyMM"));
        employeeSalaryReportDto.setEndPeriod(DateUtil.format(range.get(1),"yyyyMM"));

        employeeSalaryReportDto.setPeriodCondition(condition.contains("period"));
        employeeSalaryReportDto.setDeptCondition(condition.contains("dept"));
        employeeSalaryReportDto.setEmployeeTypeCondition(condition.contains("employeeType"));

        List<EmployeeSalaryReportVo> statistics = employeeSalaryReportService.statistics(employeeSalaryReportDto);
        return Result.success(statistics);
    }


}
