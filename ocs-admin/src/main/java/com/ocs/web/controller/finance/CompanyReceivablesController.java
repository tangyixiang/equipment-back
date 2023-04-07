package com.ocs.web.controller.finance;

import cn.hutool.core.date.DateUtil;
import com.ocs.busi.domain.dto.CompanyReceivablesDto;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.mapper.CompanyReceivablesMapper;
import com.ocs.busi.service.BankFlowService;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.busi.service.FinancePeriodService;
import com.ocs.busi.task.FlowTask;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.utils.TemplateDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/company/receivable")
public class CompanyReceivablesController extends BaseController {

    @Autowired
    private CompanyReceivablesService companyReceivablesService;
    @Autowired
    private CompanyReceivablesMapper companyReceivablesMapper;
    @Autowired
    private BankFlowService bankFlowService;
    @Autowired
    private FlowTask flowTask;
    @Autowired
    private FinancePeriodService financePeriodService;


    @RequestMapping("/template/download")
    public void templateDownload(String fileName, HttpServletResponse response) {
        TemplateDownloadUtils.downloadByFileName("应收单初始化导入模板.xlsx", response);
    }

    @RequestMapping("/upload")
    public Result importReceivables(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        companyReceivablesService.importReceivables(inputStream);
        return Result.success();
    }

    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CompanyReceivablesDto companyReceivablesDto) {
        startPage();
        List<CompanyReceivables> list = companyReceivablesMapper.findByCondition(companyReceivablesDto);
        return getDataTable(list);
    }

    @PostMapping("/match")
    @Transactional
    public Result matchFlow(@RequestBody CompanyReceivablesDto companyReceivablesDto) {
        List<String> receivablesIds = companyReceivablesDto.getReceivablesIds();
        List<String> bankFlowIds = companyReceivablesDto.getBankFlowIds();

        List<CompanyReceivables> receivablesList = companyReceivablesService.listByIds(receivablesIds);
        List<CompanyReceivables> companyReceivablesList = receivablesList.stream().filter(receivable -> !receivable.getReconciliationFlag().equals(CommonConstants.RECONCILED)).collect(Collectors.toList());

        if (companyReceivablesList.size() == 0) {
            throw new ServiceException("不存在未对账的应收账单");
        }

        List<BankFlow> bankFlowList = bankFlowService.listByIds(bankFlowIds);


        String oldState = receivablesList.stream().map(CompanyReceivables::getReconciliationFlag).collect(Collectors.joining(","));

        FinancePeriod financePeriod = financePeriodService.lambdaQuery().eq(FinancePeriod::getOpen, true).one();
        if (financePeriod == null) {
            throw new ServiceException("会计期间不存在");
        }

        String period = financePeriod.getPeriod();

        String today = DateUtil.format(new Date(), "yyyyMMdd");
        logger.info("开始手动对账");
        flowTask.bankFlowMatch(today, CommonConstants.MANUAL_RECONCILIATION, bankFlowList, receivablesList, "equal", period);
        flowTask.bankFlowMatch(today, CommonConstants.MANUAL_RECONCILIATION, bankFlowList, receivablesList, "gt", period);
        flowTask.multiBankFlowMatch(today, CommonConstants.MANUAL_RECONCILIATION, receivablesList, bankFlowList, period);

        List<CompanyReceivables> newReceivablesList = companyReceivablesService.listByIds(receivablesIds);
        String newState = newReceivablesList.stream().map(CompanyReceivables::getReconciliationFlag).collect(Collectors.joining(","));

        if (oldState.equals(newState)) {
            throw new ServiceException("对账失败");
        }

        return Result.success();
    }

    @PostMapping("/cancel")
    @Transactional
    public Result cancel(@RequestBody CompanyReceivablesDto companyReceivablesDto) {
        List<String> receivablesIds = companyReceivablesDto.getReceivablesIds();
        companyReceivablesService.cancel(receivablesIds);
        return Result.success();
    }

    @PostMapping("/delInitData")
    @Transactional
    public Result delInitData(@RequestBody CompanyReceivablesDto companyReceivablesDto) {
        List<String> receivablesIds = companyReceivablesDto.getReceivablesIds();
        companyReceivablesService.cancel(receivablesIds);
        companyReceivablesService.removeBatchByIds(receivablesIds);
        return Result.success();
    }

}
