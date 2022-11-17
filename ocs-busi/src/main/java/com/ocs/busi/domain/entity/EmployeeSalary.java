package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 员工工资
 * @TableName employee_salary
 */
@Data
@TableName(value ="employee_salary")
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
     * 职员编号
     */
    @TableId
    @NotBlank(message = "职员编号不能为空")
    private String employeeCode;

    /**
     * 职员名称
     */
    @NotBlank(message = "职员名称不能为空")
    private String employeeName;

    /**
     * 岗位工资
     */
    @NotNull(message = "岗位工资不能为空")
    private Double postSalary;

    /**
     * 薪级工资
     */
    @NotNull(message = "薪级工资不能为空")
    private Double rankSalary;

    /**
     * 基础性绩效工资
     */
    @NotNull(message = "基础性绩效工资不能为空")
    private Double performanceSalary;

    /**
     * 住房物业服务补贴
     */
    @NotNull(message = "住房物业服务补贴不能为空")
    private Double housingSalary;

    /**
     * 奖励性绩效
     */
    private Double bonusSalary;

    /**
     * 预留增项1
     */
    private Double extraIncreaseSalary1;

    /**
     * 预留增项2
     */
    private Double extraIncreaseSalary2;

    /**
     * 预留增项3
     */
    private Double extraIncreaseSalary3;

    /**
     * 预留增项4
     */
    private Double extraIncreaseSalary4;

    /**
     * 预留增项5
     */
    private Double extraIncreaseSalary5;

    /**
     * 预发 百分比
     */
    private String paymentRatio;

    /**
     * 差额
     */
    private Double unPaymentSalary;

    /**
     * 应发工资
     */
    @NotNull(message = "应发工资不能为空")
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
     * 职业年金
     */
    private Double occupationalAnnuity;

    /**
     * 职业年金清算差额
     */
    private Double occupationalAnnuityDifferent;

    /**
     * 工会费
     */
    private Double unionFees;

    /**
     * 个人所得税
     */
    private Double individualIncomeTax;

    /**
     * 预留减项1
     */
    private Double extraDecreaseSalary1;

    /**
     * 预留减项2
     */
    private Double extraDecreaseSalary2;

    /**
     * 预留减项3
     */
    private Double extraDecreaseSalary3;

    /**
     * 预留减项4
     */
    private Double extraDecreaseSalary4;

    /**
     * 预留减项5
     */
    private Double extraDecreaseSalary5;

    /**
     * 扣款合计
     */
    @NotNull(message = "扣款合计不能为空")
    private Double decreaseTotalSalary;

    /**
     * 实发金额
     */
    @NotNull(message = "实发金额不能为空")
    private Double actualAmount;

    /**
     * 备注
     */
    private String remark;

}
