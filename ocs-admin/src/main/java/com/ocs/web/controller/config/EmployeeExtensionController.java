package com.ocs.web.controller.config;

import com.ocs.busi.domain.entity.SysUserExtension;
import com.ocs.busi.service.SysUserExtensionService;
import com.ocs.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee/extension")
public class EmployeeExtensionController {

    @Autowired
    private SysUserExtensionService SysUserExtensionService;

    @PostMapping("/add")
    public Result add(@RequestBody SysUserExtension sysUserExtension) {
        SysUserExtensionService.save(sysUserExtension);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result list(@PathVariable("id") Long employeeId) {
        SysUserExtension extension = SysUserExtensionService.getById(employeeId);
        return Result.success(extension);
    }


    @PostMapping("/update")
    public Result update(@RequestBody @Validated SysUserExtension sysUserExtension) {
        SysUserExtensionService.updateById(sysUserExtension);
        return Result.success();
    }

}
