package com.ocs.busi.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ocs.busi.config.DoubleSerializerConfig;
import com.ocs.common.annotation.Excel;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.List;

@Data
public class EmployeeSalaryReportVo {

    private String id;

    @Excel(name = "分组", align = HorizontalAlignment.LEFT)
    private String name;

    private String type;

    // private EmployeeSalary employeeSalary;
    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "岗位工资", cellType = Excel.ColumnType.NUMERIC)
    private Double postSalary = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "补岗位工资", cellType = Excel.ColumnType.NUMERIC)
    private Double additionPostSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "薪级工资", cellType = Excel.ColumnType.NUMERIC)
    private Double rankSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "补薪级工资", cellType = Excel.ColumnType.NUMERIC)
    private Double additionRankSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "基础性绩效工资", cellType = Excel.ColumnType.NUMERIC)
    private Double performanceSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "补基础性绩效工资", cellType = Excel.ColumnType.NUMERIC)
    private Double additionPerformanceSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留增项1", cellType = Excel.ColumnType.NUMERIC)
    private Double extraIncreaseSalary1 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留增项2", cellType = Excel.ColumnType.NUMERIC)
    private Double extraIncreaseSalary2 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留增项3", cellType = Excel.ColumnType.NUMERIC)
    private Double extraIncreaseSalary3 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留增项4", cellType = Excel.ColumnType.NUMERIC)
    private Double extraIncreaseSalary4 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留增项5", cellType = Excel.ColumnType.NUMERIC)
    private Double extraIncreaseSalary5 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "应发合计", cellType = Excel.ColumnType.NUMERIC)
    private Double salaryTotal;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "奖励性绩效", cellType = Excel.ColumnType.NUMERIC)
    private Double bonusSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "住房物业服务补贴", cellType = Excel.ColumnType.NUMERIC)
    private Double housingSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "应发工资", cellType = Excel.ColumnType.NUMERIC)
    private Double salaryPayable;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "养老保险", cellType = Excel.ColumnType.NUMERIC)
    private Double endowmentInsurance;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "医保", cellType = Excel.ColumnType.NUMERIC)
    private Double medicalInsurance;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "失业保险", cellType = Excel.ColumnType.NUMERIC)
    private Double unemploymentInsurance;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "公积金", cellType = Excel.ColumnType.NUMERIC)
    private Double housingAccumulationFunds;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "工会费", cellType = Excel.ColumnType.NUMERIC)
    private Double unionFees;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "职业年金", cellType = Excel.ColumnType.NUMERIC)
    private Double occupationalAnnuity;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留减项1", cellType = Excel.ColumnType.NUMERIC)
    private Double extraDecreaseSalary1 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留减项2", cellType = Excel.ColumnType.NUMERIC)
    private Double extraDecreaseSalary2 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留减项3", cellType = Excel.ColumnType.NUMERIC)
    private Double extraDecreaseSalary3 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留减项4", cellType = Excel.ColumnType.NUMERIC)
    private Double extraDecreaseSalary4 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "预留减项5", cellType = Excel.ColumnType.NUMERIC)
    private Double extraDecreaseSalary5 = 0d;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "扣款合计", cellType = Excel.ColumnType.NUMERIC)
    private Double decreaseTotalSalary;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "个人所得税", cellType = Excel.ColumnType.NUMERIC)
    private Double individualIncomeTax;

    @JsonSerialize(using = DoubleSerializerConfig.class)
    @Excel(name = "实发金额", cellType = Excel.ColumnType.NUMERIC)
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
