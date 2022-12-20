package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.EmployeeSalary;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.EmployeeSalaryService;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.StringUtils;
import com.ocs.common.utils.TemplateDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/8
 */
@RestController
@RequestMapping("/employee/salary")
public class EmployeeSalaryController extends BaseController {

    @Autowired
    private EmployeeSalaryService employeeSalaryService;


    @RequestMapping("/upload")
    public Result importSalary(MultipartFile file, String period) throws IOException {
        InputStream inputStream = file.getInputStream();
        employeeSalaryService.importSalary(inputStream, period);
        return Result.success();
    }

    @RequestMapping("/template/download")
    public void templateDownload(String fileName, HttpServletResponse response) {
        TemplateDownloadUtils.downloadByFileName("工资导入模板.xlsx", response);
    }

    @GetMapping("/list")
    public TableDataInfo list(String salaryPeriod) {
        startPage("create_time desc");
        QueryWrapper<EmployeeSalary> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(salaryPeriod)){
            queryWrapper.eq("salary_period", salaryPeriod);
        }
        List<EmployeeSalary> list = employeeSalaryService.list(queryWrapper);
        return getDataTable(list);
    }

    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<EmployeeSalary>().deleteByIds(ids, employeeSalaryService);
        return Result.success();
    }
}
