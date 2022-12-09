package com.ocs.busi.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmployeeSalaryReportDto {

    // 开始时间条件
    @NotBlank(message = "开始期间不能为空")
    private String startPeriod;

    // 结束时间条件
    @NotBlank(message = "结束期间不能为空")
    private String endPeriod;

    // 期间
    private boolean periodCondition;

    // 部门
    private boolean deptCondition;

    // 人员性质
    private boolean employeeTypeCondition;
}
