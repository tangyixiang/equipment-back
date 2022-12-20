package com.ocs.busi.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployeeSalaryReportDto {

    // 开始时间条件
    private String startPeriod;

    // 结束时间条件
    private String endPeriod;

    // 期间
    private boolean periodCondition;

    // 部门
    private boolean deptCondition;

    // 人员性质
    private boolean employeeTypeCondition;

    // 人员名字
    private boolean employeeNameCondition;

    private List<LocalDateTime> range;

    private List<String> condition;

}
