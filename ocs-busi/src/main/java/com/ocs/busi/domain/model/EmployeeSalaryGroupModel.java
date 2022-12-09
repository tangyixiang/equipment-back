package com.ocs.busi.domain.model;

import com.ocs.busi.domain.entity.EmployeeSalary;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeSalaryGroupModel {

    private String name;

    private String type;

    private List<EmployeeSalary> list;
    // 子级
    private List<EmployeeSalaryGroupModel> childModel;
}
