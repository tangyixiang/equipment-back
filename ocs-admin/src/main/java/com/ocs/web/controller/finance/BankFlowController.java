package com.ocs.web.controller.finance;

import com.ocs.busi.domain.dto.BankFlowDto;
import com.ocs.busi.domain.dto.BankFlowUploadDto;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.mapper.BankFlowMapper;
import com.ocs.busi.service.BankFlowService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.TemplateDownloadUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private BankFlowMapper bankFlowMapper;


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

    @PostMapping("/list")
    public TableDataInfo list(@RequestBody BankFlowDto bankFlowDto) {
        startPage("create_time desc");
        if (ObjectUtils.isNotEmpty(bankFlowDto.getTradeTimeArray())) {
            bankFlowDto.setTradeTimeStart(LocalDateTime.of(bankFlowDto.getTradeTimeArray().get(0), LocalTime.MIN));
            bankFlowDto.setTradeTimeEnd(LocalDateTime.of(bankFlowDto.getTradeTimeArray().get(1), LocalTime.of(23, 59, 59)));
        }

        List<BankFlow> list = bankFlowMapper.findByCondition(bankFlowDto);
        return getDataTable(list);
    }

    @PostMapping("/listUnReconcile")
    public TableDataInfo listUnReconcile(@RequestBody BankFlowDto bankFlowDto) {
        startPage("create_time desc");
        if (ObjectUtils.isNotEmpty(bankFlowDto.getTradeTimeArray())) {
            bankFlowDto.setTradeTimeStart(LocalDateTime.of(bankFlowDto.getTradeTimeArray().get(0), LocalTime.MIN));
            bankFlowDto.setTradeTimeEnd(LocalDateTime.of(bankFlowDto.getTradeTimeArray().get(1), LocalTime.of(23, 59, 59)));
        }
        bankFlowDto.setTradeType(CommonConstants.LOAN);
        List<BankFlow> list = bankFlowMapper.findByCondition(bankFlowDto);
        return getDataTable(list);
    }

}
