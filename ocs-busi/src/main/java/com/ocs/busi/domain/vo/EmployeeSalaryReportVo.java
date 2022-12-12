package com.ocs.busi.domain.vo;

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

    public EmployeeSalaryReportVo() {
    }

    public EmployeeSalaryReportVo(Double postSalary, Double additionPostSalary, Double rankSalary, Double additionRankSalary,
                          Double performanceSalary, Double additionPerformanceSalary,
                          Double extraIncreaseSalary1, Double extraIncreaseSalary2, Double extraIncreaseSalary3,
                          Double extraIncreaseSalary4, Double extraIncreaseSalary5, Double salaryTotal,
                          Double bonusSalary, Double housingSalary, Double salaryPayable, Double endowmentInsurance,
                          Double medicalInsurance, Double unemploymentInsurance, Double housingAccumulationFunds, Double unionFees,
                          Double occupationalAnnuity, Double extraDecreaseSalary1, Double extraDecreaseSalary2, Double extraDecreaseSalary3,
                          Double extraDecreaseSalary4, Double extraDecreaseSalary5, Double decreaseTotalSalary, Double individualIncomeTax, Double actualAmount) {
        this.postSalary = postSalary;
        this.additionPostSalary = additionPostSalary;
        this.rankSalary = rankSalary;
        this.additionRankSalary = additionRankSalary;
        this.performanceSalary = performanceSalary;
        this.additionPerformanceSalary = additionPerformanceSalary;
        this.extraIncreaseSalary1 = extraIncreaseSalary1;
        this.extraIncreaseSalary2 = extraIncreaseSalary2;
        this.extraIncreaseSalary3 = extraIncreaseSalary3;
        this.extraIncreaseSalary4 = extraIncreaseSalary4;
        this.extraIncreaseSalary5 = extraIncreaseSalary5;
        this.salaryTotal = salaryTotal;
        this.bonusSalary = bonusSalary;
        this.housingSalary = housingSalary;
        this.salaryPayable = salaryPayable;
        this.endowmentInsurance = endowmentInsurance;
        this.medicalInsurance = medicalInsurance;
        this.unemploymentInsurance = unemploymentInsurance;
        this.housingAccumulationFunds = housingAccumulationFunds;
        this.unionFees = unionFees;
        this.occupationalAnnuity = occupationalAnnuity;
        this.extraDecreaseSalary1 = extraDecreaseSalary1;
        this.extraDecreaseSalary2 = extraDecreaseSalary2;
        this.extraDecreaseSalary3 = extraDecreaseSalary3;
        this.extraDecreaseSalary4 = extraDecreaseSalary4;
        this.extraDecreaseSalary5 = extraDecreaseSalary5;
        this.decreaseTotalSalary = decreaseTotalSalary;
        this.individualIncomeTax = individualIncomeTax;
        this.actualAmount = actualAmount;
    }
}
