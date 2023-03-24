package com.ocs.busi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.busi.domain.dto.BankFlowUploadDto;
import com.ocs.busi.domain.entity.BankFlow;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface BankFlowService extends IService<BankFlow> {

    Map<String, Object> uploadValidate(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto);

    void importBankFlow(InputStream inputStream, BankFlowUploadDto bankFlowUploadDto);

    List<BankFlow> countPriceBeforePeriod(String period);

}
