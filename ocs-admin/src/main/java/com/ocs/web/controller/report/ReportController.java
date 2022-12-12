package com.ocs.web.controller.report;

import cn.hutool.core.date.DateUtil;
import com.ocs.busi.domain.dto.EmployeeSalaryReportDto;
import com.ocs.busi.domain.vo.EmployeeSalaryReportVo;
import com.ocs.busi.report.EmployeeSalaryReportService;
import com.ocs.common.core.domain.Result;
import com.ocs.common.utils.StringUtils;
import com.ocs.common.utils.poi.ExcelUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private EmployeeSalaryReportService employeeSalaryReportService;

    @PostMapping("/employee/salary")
    public Result employeeSalary(@RequestBody EmployeeSalaryReportDto employeeSalaryReportDto) {

        if (ObjectUtils.isEmpty(employeeSalaryReportDto.getRange()) || ObjectUtils.isEmpty(employeeSalaryReportDto.getCondition())) {
            return Result.success(Collections.emptyList());
        }

        List<LocalDateTime> range = employeeSalaryReportDto.getRange();
        List<String> condition = employeeSalaryReportDto.getCondition();

        employeeSalaryReportDto.setStartPeriod(DateUtil.format(range.get(0), "yyyyMM"));
        employeeSalaryReportDto.setEndPeriod(DateUtil.format(range.get(1), "yyyyMM"));
        employeeSalaryReportDto.setPeriodCondition(condition.contains("period"));
        employeeSalaryReportDto.setDeptCondition(condition.contains("dept"));
        employeeSalaryReportDto.setEmployeeTypeCondition(condition.contains("employeeType"));

        List<EmployeeSalaryReportVo> statistics = employeeSalaryReportService.statistics(employeeSalaryReportDto);

        EmployeeSalaryReportVo totalEmployeeSalaryReportVo = null;

        if (statistics.size() > 0) {
            totalEmployeeSalaryReportVo = summary(statistics).get();
            totalEmployeeSalaryReportVo.setName("合计");
            statistics.add(totalEmployeeSalaryReportVo);
        }

        return Result.success(statistics);
    }


    @PostMapping("/employee/salary/result")
    public void employeeSalaryResult(@RequestBody EmployeeSalaryReportDto employeeSalaryReportDto, HttpServletResponse response) {
        Result result = employeeSalary(employeeSalaryReportDto);
        List<EmployeeSalaryReportVo> exportList = new ArrayList<>();
        List<EmployeeSalaryReportVo> list = (List<EmployeeSalaryReportVo>) result.get(Result.DATA_TAG);
        // 空格构建层次
        for (EmployeeSalaryReportVo vo : list) {
            AtomicInteger loop = new AtomicInteger(0);
            exportList.add(vo);
            flat(vo.getChildren(), exportList, loop);
        }

        ExcelUtil<EmployeeSalaryReportVo> util = new ExcelUtil<>(EmployeeSalaryReportVo.class);
        util.exportExcel(response, exportList, "分录数据");
    }

    private void flat(List<EmployeeSalaryReportVo> list, List<EmployeeSalaryReportVo> exportList, AtomicInteger loop) {
        if (ObjectUtils.isNotEmpty(list)) {
            int i = loop.incrementAndGet();
            for (EmployeeSalaryReportVo vo : list) {
                vo.setName(StringUtils.leftPad(" ", i * 4) + vo.getName());
                exportList.add(vo);
                flat(vo.getChildren(), exportList, new AtomicInteger(i));
            }
        }
    }


    private Optional<EmployeeSalaryReportVo> summary(List<EmployeeSalaryReportVo> list) {

        Optional<EmployeeSalaryReportVo> EmployeeSalaryTotalOptional = list.stream().reduce((a, b) -> new EmployeeSalaryReportVo(
                a.getPostSalary() + b.getPostSalary(),
                a.getAdditionPostSalary() + b.getAdditionPostSalary(),
                a.getRankSalary() + b.getRankSalary(),
                a.getAdditionRankSalary() + b.getAdditionRankSalary(),
                a.getPerformanceSalary() + b.getPerformanceSalary(),
                a.getAdditionPerformanceSalary() + b.getAdditionPerformanceSalary(),
                a.getExtraIncreaseSalary1() + b.getExtraIncreaseSalary1(),
                a.getExtraIncreaseSalary2() + b.getExtraIncreaseSalary2(),
                a.getExtraIncreaseSalary3() + b.getExtraIncreaseSalary3(),
                a.getExtraIncreaseSalary4() + b.getExtraIncreaseSalary4(),
                a.getExtraIncreaseSalary5() + b.getExtraIncreaseSalary5(),
                a.getSalaryTotal() + b.getSalaryTotal(),
                a.getBonusSalary() + b.getBonusSalary(),
                a.getHousingSalary() + b.getBonusSalary(),
                a.getSalaryPayable() + b.getRankSalary(),
                a.getEndowmentInsurance() + b.getEndowmentInsurance(),
                a.getMedicalInsurance() + b.getMedicalInsurance(),
                a.getUnemploymentInsurance() + b.getMedicalInsurance(),
                a.getHousingAccumulationFunds() + b.getHousingAccumulationFunds(),
                a.getUnionFees() + b.getUnionFees(),
                a.getOccupationalAnnuity() + b.getOccupationalAnnuity(),
                a.getExtraDecreaseSalary1() + b.getExtraDecreaseSalary1(),
                a.getExtraDecreaseSalary2() + b.getExtraDecreaseSalary2(),
                a.getExtraDecreaseSalary3() + b.getExtraDecreaseSalary3(),
                a.getExtraDecreaseSalary4() + b.getExtraDecreaseSalary4(),
                a.getExtraDecreaseSalary5() + b.getExtraDecreaseSalary5(),
                a.getDecreaseTotalSalary() + b.getDecreaseTotalSalary(),
                a.getIndividualIncomeTax() + b.getIndividualIncomeTax(),
                a.getActualAmount() + b.getActualAmount()
        ));
        return EmployeeSalaryTotalOptional;
    }


}
