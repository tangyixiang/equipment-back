package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.dto.BankFlowUploadDto;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.service.BankFlowService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.TemplateDownloadUtils;
import com.ocs.common.utils.sql.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author tangyixiang
 * @Date 2022/11/4
 */
@RestController
@RequestMapping("/bank/flow")
public class BankFlowController extends BaseController {

    @Autowired
    private BankFlowService bankFlowService;

    @RequestMapping("/uploadValidate")
    public Result uploadValidate(MultipartFile file, BankFlowUploadDto bankFlowUploadDto) throws IOException {
        InputStream inputStream = file.getInputStream();
        Map<String, Object> map = bankFlowService.uploadValidate(inputStream, bankFlowUploadDto);
        return Result.success(map);
    }


    @RequestMapping("/upload")
    public Result importBankFlow(MultipartFile file, BankFlowUploadDto bankFlowUploadDto) throws IOException {
        InputStream inputStream = file.getInputStream();
        bankFlowService.importBankFlow(inputStream, bankFlowUploadDto);
        return Result.success();
    }


    @RequestMapping("/template/download")
    public void templateDownload(String fileName, HttpServletResponse response) {
        TemplateDownloadUtils.downloadByFileName("银行流水导入模板.xlsx", response);
    }

    @GetMapping("/list")
    public TableDataInfo list(BankFlow bankFlow) {
        startPage("create_time desc");
        QueryWrapper<BankFlow> queryWrapper = QueryUtil.dynamicCondition(bankFlow, CommonConstants.QUERY_LIKE, false);
        List<BankFlow> list = bankFlowService.list(queryWrapper);
        return getDataTable(list);
    }


}
