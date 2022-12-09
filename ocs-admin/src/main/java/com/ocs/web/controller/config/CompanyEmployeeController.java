package com.ocs.web.controller.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.ocs.busi.domain.dto.CompanyEmployeeDto;
import com.ocs.busi.domain.entity.CompanyEmployee;
import com.ocs.busi.domain.entity.SysUserExtension;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.CompanyEmployeeService;
import com.ocs.busi.service.SysUserExtensionService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.helper.QueryHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/company/employee")
public class CompanyEmployeeController extends BaseController {

    @Autowired
    private CompanyEmployeeService companyEmployeeService;
    @Autowired
    private SysUserExtensionService userExtensionService;


    @PostMapping("/add")
    @Transactional
    public Result add(@RequestBody @Validated CompanyEmployeeDto companyEmployeeDto) {

        CompanyEmployee companyEmployee = new CompanyEmployee();
        BeanUtils.copyProperties(companyEmployeeDto, companyEmployee);

        SysUserExtension sysUserExtension = new SysUserExtension();
        BeanUtils.copyProperties(companyEmployeeDto, sysUserExtension);

        long id = IdUtil.getSnowflakeNextId();
        companyEmployee.setId(id);
        companyEmployee.setDel(CommonConstants.STATUS_NORMAL);
        sysUserExtension.setUseId(id);

        companyEmployeeService.save(companyEmployee);
        userExtensionService.save(sysUserExtension);
        return Result.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(CompanyEmployee companyEmployee) {
        startPage();
        QueryWrapper<CompanyEmployee> wrapper = QueryHelper.dynamicCondition(companyEmployee, CommonConstants.QUERY_LIKE, false);
        List<CompanyEmployee> list = companyEmployeeService.list(wrapper);
        List<CompanyEmployeeDto> dtoList = new ArrayList<>();
        for (CompanyEmployee employee : list) {
            CompanyEmployeeDto dto = new CompanyEmployeeDto();
            BeanUtils.copyProperties(employee, dto);
            SysUserExtension sysUserExtension = userExtensionService.getById(employee.getId());
            if (sysUserExtension != null) {
                dto.setCertificate(sysUserExtension.getCertificate());
                dto.setDimensions(sysUserExtension.getDimensions());
            }
            dtoList.add(dto);
        }

        TableDataInfo dataTable = getDataTable(dtoList);
        dataTable.setTotal(new PageInfo(list).getTotal());
        return dataTable;
    }


    @PostMapping("/update")
    @Transactional
    public Result update(@RequestBody @Validated CompanyEmployeeDto companyEmployeeDto) {

        CompanyEmployee companyEmployee = new CompanyEmployee();
        BeanUtils.copyProperties(companyEmployeeDto, companyEmployee);

        SysUserExtension sysUserExtension = new SysUserExtension();
        BeanUtils.copyProperties(companyEmployeeDto, sysUserExtension);

        sysUserExtension.setUseId(companyEmployee.getId());

        // userExtensionService.remove(new LambdaQueryWrapper<SysUserExtension>().eq(SysUserExtension::getUseId, companyEmployee.getId()));

        companyEmployeeService.updateById(companyEmployee);
        userExtensionService.saveOrUpdate(sysUserExtension);
        return Result.success();
    }


    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<CompanyEmployee>().deleteByIds(ids, companyEmployeeService);
        return Result.success();
    }

}
