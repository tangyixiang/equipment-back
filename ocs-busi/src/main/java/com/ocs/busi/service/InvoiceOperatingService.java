package com.ocs.busi.service;

import com.ocs.busi.domain.entity.InvoiceOperating;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
* @author tangyx
* @createDate 2022-11-07 11:58:55
*/
public interface InvoiceOperatingService extends IService<InvoiceOperating> {

    void importInvoice(InputStream inputStream, String period);

    List<String> monthPeriod(String month);

    Map<String, Object> uploadValidate(InputStream inputStream, String period);
}
