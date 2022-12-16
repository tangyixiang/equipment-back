package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.CompanyReceivablesMapper;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangyx
 * @createDate 2022-12-16 11:15:50
 */
@Service
public class CompanyReceivablesServiceImpl extends ServiceImpl<CompanyReceivablesMapper, CompanyReceivables>
        implements CompanyReceivablesService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyReceivablesServiceImpl.class);

    @Autowired
    private CompanyClientOrgService clientOrgService;

    @Override
    @Transactional
    public void importReceivables(InputStream inputStream) {
        List<CompanyReceivables> receivablesList = convertExcelToReceivables(inputStream);
        for (CompanyReceivables companyReceivables : receivablesList) {
            List<CompanyClientOrg> clientOrgList = clientOrgService.list(new LambdaQueryWrapper<CompanyClientOrg>().eq(CompanyClientOrg::getName, companyReceivables.getClientOrgName())
                    .eq(CompanyClientOrg::getDel, CommonConstants.STATUS_NORMAL));
            if (clientOrgList.size() == 0) {
                throw new ServiceException("客户名称:" + companyReceivables.getClientOrgName() + ",不存在");
            }
        }
        saveBatch(receivablesList);
    }

    private List<CompanyReceivables> convertExcelToReceivables(InputStream inputStream) {
        List<CompanyReceivables> receivablesList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(receivablesList));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("财政发票导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板");
        }
        return receivablesList;
    }

    private RowHandler createRowHandler(List<CompanyReceivables> receivablesList) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }

            CompanyReceivables companyReceivables = new CompanyReceivables();
            companyReceivables.setId(IdUtil.getSnowflakeNextIdStr());
            companyReceivables.setPeriod(convertString(rowlist.get(0)));
            companyReceivables.setSourceType(CommonConstants.RECEIVABLE_CUSTOM);
            companyReceivables.setClientOrgName(convertString(rowlist.get(2)));
            companyReceivables.setReceivableAmount(covertDouble(convertString(rowlist.get(3))));
            companyReceivables.setInvoicingDate(LocalDate.parse(convertString(rowlist.get(4))));
            companyReceivables.setReconciliationFlag(CommonConstants.NOT_RECONCILED);

            ValidateHelper.validData(companyReceivables);
            receivablesList.add(companyReceivables);
        };
    }

    public String convertString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Double covertDouble(String value) {
        return StringUtils.isEmpty(value) || value.equals("null") ? null : Double.parseDouble(value);
    }
}



