package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyEmployee;
import com.ocs.busi.service.CompanyEmployeeService;
import com.ocs.busi.mapper.CompanyEmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author tangyx
* @description 针对表【company_employee(职员信息表)】的数据库操作Service实现
* @createDate 2022-11-29 12:12:56
*/
@Service
public class CompanyEmployeeServiceImpl extends ServiceImpl<CompanyEmployeeMapper, CompanyEmployee>
    implements CompanyEmployeeService{

}




