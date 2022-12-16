package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.helper.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/company/receivable")
public class CompanyReceivablesController extends BaseController {

    @Autowired
    private CompanyReceivablesService CompanyReceivablesService;

    @RequestMapping("/upload")
    public Result importReceivables(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        CompanyReceivablesService.importReceivables(inputStream);
        return Result.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(CompanyReceivables CompanyReceivables) {
        startPage();
        QueryWrapper<CompanyReceivables> wrapper = QueryHelper.dynamicCondition(CompanyReceivables, CommonConstants.QUERY_LIKE, false);
        List<CompanyReceivables> list = CompanyReceivablesService.list(wrapper);
        return getDataTable(list);
    }

}
