package com.ocs.busi.domain.vo;

import cn.hutool.core.util.IdUtil;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeSalaryReportVo {

    private String id;

    private String name;

    private String type;

    // private EmployeeSalary employeeSalary;

    private Double postSalary = 0d;

    private Double additionPostSalary;


    private Double rankSalary;


    private Double additionRankSalary;

    private Double performanceSalary;


    private Double additionPerformanceSalary;

    private Double extraIncreaseSalary1 = 0d;

    private Double extraIncreaseSalary2 = 0d;


    private Double extraIncreaseSalary3 = 0d;


    private Double extraIncreaseSalary4 = 0d;

    private Double extraIncreaseSalary5 = 0d;

    private Double salaryTotal;


    private Double bonusSalary;

    private Double housingSalary;


    private Double salaryPayable;


    private Double endowmentInsurance;

    private Double medicalInsurance;

    private Double unemploymentInsurance;

    private Double housingAccumulationFunds;


    private Double unionFees;

    private Double occupationalAnnuity;


    private Double extraDecreaseSalary1 = 0d;


    private Double extraDecreaseSalary2 = 0d;


    private Double extraDecreaseSalary3 = 0d;


    private Double extraDecreaseSalary4 = 0d;


    private Double extraDecreaseSalary5 = 0d;

    private Double decreaseTotalSalary;


    private Double individualIncomeTax;


    private Double actualAmount;

    private List<EmployeeSalaryReportVo> children;
}
