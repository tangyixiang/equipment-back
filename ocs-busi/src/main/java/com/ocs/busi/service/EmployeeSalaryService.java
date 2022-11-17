package com.ocs.busi.service;

import com.ocs.busi.domain.entity.EmployeeSalary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;

/**
* @author tangyx
* @description 针对表【employee_salary(员工工资)】的数据库操作Service
* @createDate 2022-11-07 11:58:55
*/
public interface EmployeeSalaryService extends IService<EmployeeSalary> {

    void importSalary(InputStream inputStream, String period);
}
