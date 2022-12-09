package com.ocs.web.controller.report;

import com.ocs.busi.domain.dto.EmployeeSalaryReportDto;
import com.ocs.busi.domain.vo.EmployeeSalaryReportVo;
import com.ocs.busi.report.EmployeeSalaryReportService;
import com.ocs.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private EmployeeSalaryReportService employeeSalaryReportService;

    @PostMapping("/employee/salary")
    public Result employeeSalary(@RequestBody EmployeeSalaryReportDto employeeSalaryReportDto) {
        List<EmployeeSalaryReportVo> statistics = employeeSalaryReportService.statistics(employeeSalaryReportDto);
        return Result.success(statistics);
    }
}
