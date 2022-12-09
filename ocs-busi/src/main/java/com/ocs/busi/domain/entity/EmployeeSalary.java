package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 员工工资
 *
 * @TableName employee_salary
 */
@Data
@TableName(value = "employee_salary")
public class EmployeeSalary extends SimpleEntity {
    /**
     * id
     */
    private String id;

    /**
     * 工资期间
     */
    @NotBlank(message = "工资期间不能为空")
    private String salaryPeriod;

    /**
     * 职员名称
     */
    @NotBlank(message = "职员名称不能为空")
    private String employeeName;


    private String employeeType;

    /**
     * 部门
     */
    @NotBlank(message = "部门不能为空")
    private String employeeDept;

    /**
     * 部门Id
     */
    private Long employeeDeptId;

    /**
     * 账户
     */
    @NotBlank(message = "账户不能为空")
    private String employeeAccountNo;

    /**
     * 居民身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    private String employeeIdNumber;

    /**
     * 岗位工资
     */
    @NotNull(message = "岗位工资不能为空")
    private Double postSalary = 0d;


    @NotNull(message = "补岗位工资不能为空")
    private Double additionPostSalary;

    /**
     * 薪级工资
     */
    @NotNull(message = "薪级工资不能为空")
    private Double rankSalary;


    @NotNull(message = "补薪级工资不能为空")
    private Double additionRankSalary;

    /**
     * 基础性绩效工资
     */
    @NotNull(message = "基础性绩效工资不能为空")
    private Double performanceSalary;

    /**
     * 基础性绩效工资
     */
    @NotNull(message = "补基础性绩效工资不能为空")
    private Double additionPerformanceSalary;

    /**
     * 预留增项1
     */
    private Double extraIncreaseSalary1 = 0d;

    /**
     * 预留增项2
     */
    private Double extraIncreaseSalary2 = 0d;

    /**
     * 预留增项3
     */
    private Double extraIncreaseSalary3 = 0d;

    /**
     * 预留增项4
     */
    private Double extraIncreaseSalary4 = 0d;

    /**
     * 预留增项5
     */
    private Double extraIncreaseSalary5 = 0d;

    /**
     * 合计
     */
    @NotNull(message = "合计不能为空")
    private Double salaryTotal;


    /**
     * 激励性绩效
     */
    @NotNull(message = "激励性绩效不能为空")
    private Double bonusSalary;

    /**
     * 住房物业服务补贴
     */
    @NotNull(message = "住房物业服务补贴不能为空")
    private Double housingSalary;


    /**
     * 应发工资
     */
    @NotNull(message = "应发合计不能为空")
    private Double salaryPayable;

    /**
     * 养老保险
     */
    @NotNull(message = "养老保险不能为空")
    private Double endowmentInsurance;

    /**
     * 医保
     */
    @NotNull(message = "医保不能为空")
    private Double medicalInsurance;

    /**
     * 失业保险
     */
    @NotNull(message = "失业保险不能为空")
    private Double unemploymentInsurance;

    /**
     * 公积金
     */
    @NotNull(message = "公积金不能为空")
    private Double housingAccumulationFunds;

    /**
     * 工会费
     */
    @NotNull(message = "工会会费不能为空")
    private Double unionFees;

    /**
     * 职业年金
     */
    @NotNull(message = "职业年金不能为空")
    private Double occupationalAnnuity;


    /**
     * 预留减项1
     */
    private Double extraDecreaseSalary1 = 0d;

    /**
     * 预留减项2
     */
    private Double extraDecreaseSalary2 = 0d;

    /**
     * 预留减项3
     */
    private Double extraDecreaseSalary3 = 0d;

    /**
     * 预留减项4
     */
    private Double extraDecreaseSalary4 = 0d;

    /**
     * 预留减项5
     */
    private Double extraDecreaseSalary5 = 0d;

    /**
     * 扣款合计
     */
    @NotNull(message = "扣款合计不能为空")
    private Double decreaseTotalSalary;

    /**
     * 个人所得税
     */
    @NotNull(message = "个税不能为空")
    private Double individualIncomeTax;

    /**
     * 实发金额
     */
    @NotNull(message = "实发金额不能为空")
    private Double actualAmount;

    /**
     * 备注
     */
    private String remark;

    public EmployeeSalary(){}

    public EmployeeSalary(Double postSalary, Double additionPostSalary, Double rankSalary, Double additionRankSalary,
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
