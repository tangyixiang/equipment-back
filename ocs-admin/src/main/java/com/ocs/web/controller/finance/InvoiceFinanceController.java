package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.TemplateDownloadUtils;
import com.ocs.common.utils.sql.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author tangyixiang
 * @Date 2022/11/8
 */
@RestController
@RequestMapping("/invoice/finance")
public class InvoiceFinanceController extends BaseController {

    @Autowired
    private InvoiceFinanceService invoiceFinanceService;


    @RequestMapping("/uploadValidate")
    public Result uploadValidate(MultipartFile file, String period) throws IOException {
        InputStream inputStream = file.getInputStream();
        Map<String, Object> map = invoiceFinanceService.uploadValidate(inputStream, period);
        return Result.success(map);
    }

    @RequestMapping("/upload")
    public Result importInvoice(MultipartFile file, String period) throws IOException {
        InputStream inputStream = file.getInputStream();
        invoiceFinanceService.importInvoice(inputStream, period);
        return Result.success();
    }

    @GetMapping("/month/period")
    public Result monthPeriod(String month) {
        List<String> list = invoiceFinanceService.monthPeriod(month);
        return Result.success(list);
    }

    @RequestMapping("/template/download")
    public void templateDownload(String fileName, HttpServletResponse response) {
        TemplateDownloadUtils.downloadByFileName("财政发票导入模板.xlsx", response);
    }

    @GetMapping("/list")
    public TableDataInfo list(InvoiceFinance invoiceFinance) {
        startPage("create_time desc");
        QueryWrapper<InvoiceFinance> queryWrapper = QueryUtil.dynamicCondition(invoiceFinance, CommonConstants.QUERY_LIKE);
        List<InvoiceFinance> list = invoiceFinanceService.list(queryWrapper);
        return getDataTable(list);
    }

    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<InvoiceFinance>().deleteByIds(ids, invoiceFinanceService);
        return Result.success();
    }
}
