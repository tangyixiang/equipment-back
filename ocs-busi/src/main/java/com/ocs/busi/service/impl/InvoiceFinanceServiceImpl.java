package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.helper.InvoiceHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.InvoiceFinanceMapper;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.busi.service.InvoiceFinanceSplitService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tangyx
 * @createDate 2022-11-07 11:58:55
 */
@Service
public class InvoiceFinanceServiceImpl extends ServiceImpl<InvoiceFinanceMapper, InvoiceFinance>
        implements InvoiceFinanceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceFinanceServiceImpl.class);

    @Value("${busi.config.bankAccount.finance}")
    private String bankAccount;

    @Autowired
    private InvoiceFinanceSplitService invoiceFinanceSplitService;
    @Autowired
    private InvoiceHelper invoiceHelper;
    @Autowired
    private CompanyReceivablesService receivablesService;

    @Override
    @Transactional
    public synchronized void importInvoice(InputStream inputStream, String period) {
        List<InvoiceFinance> invoiceFinanceList = convertExcelToInvoice(inputStream, period);
        // 数据校验
        invoiceHelper.validateFinance(invoiceFinanceList);

        delRepeatData(invoiceFinanceList);

        // 保存发票
        saveBatch(invoiceFinanceList);
        saveReceivable(invoiceFinanceList, period);
    }

    private void saveReceivable(List<InvoiceFinance> invoiceFinanceList, String period) {
        List<CompanyReceivables> receivablesList = new ArrayList<>();
        List<String> redList = invoiceFinanceList.stream().filter(invoiceFinance -> invoiceFinance.getElectronInvoiceNo() != null)
                .map(invoiceFinance -> invoiceFinance.getElectronInvoiceNo().trim()).collect(Collectors.toList());

        for (InvoiceFinance invoice : invoiceFinanceList) {
            CompanyReceivables receivables = new CompanyReceivables();
            receivables.setId(invoice.getId());
            receivables.setPeriod(invoice.getInvoicingPeriod());
            receivables.setSourceType(CommonConstants.RECEIVABLE_FINANCE);
            receivables.setBankAccount(bankAccount);
            receivables.setInvoicingDate(LocalDate.parse(invoice.getInvoicingDate()));
            receivables.setClientOrgName(invoice.getPayer());
            receivables.setReceivableAmount(Double.parseDouble(invoice.getPrice()));
            receivables.setUnConfirmAmount(Double.parseDouble(invoice.getPrice()));
            receivables.setReconciliationFlag(CommonConstants.NOT_RECONCILED);
            receivables.setValid(!redList.contains(invoice.getInvoiceId()));
            receivables.setInvoiceId(invoice.getInvoiceId());
            receivables.setDirection(invoice.getElectronInvoiceNo() == null ? CommonConstants.INVOICE_DIRECT_FORWARD : CommonConstants.INVOICE_DIRECT_REVERSE);
            receivablesList.add(receivables);
        }

        // 应收账单
        receivablesService.saveBatch(receivablesList);
    }

    private void delRepeatData(List<InvoiceFinance> invoiceFinanceList) {
        for (InvoiceFinance invoice : invoiceFinanceList) {
            Wrapper<InvoiceFinance> wrapper = new LambdaQueryWrapper<InvoiceFinance>().eq(InvoiceFinance::getInvoiceId, invoice.getInvoiceId());
            List<InvoiceFinance> list = list(wrapper);
            if (list.size() > 0) {
                logger.info("财政性发票重复导入,删除之前的,发票号码:{}", invoice.getInvoiceId());
                //TODO 后续再完善删除逻辑
                remove(wrapper);
                // 删除应收账单
                receivablesService.removeBatchByIds(list.stream().map(InvoiceFinance::getId).collect(Collectors.toList()));
            }
        }
    }

    private List<InvoiceFinance> convertExcelToInvoice(InputStream inputStream, String month) {
        List<InvoiceFinance> invoiceFinanceList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(invoiceFinanceList, month));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("财政发票导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板," + e.getMessage());
        }
        return invoiceFinanceList;
    }

    @Override
    public List<String> monthPeriod(String month) {
        return getBaseMapper().groupByInvoicingPeriod();
    }

    @Override
    public Map<String, Object> uploadValidate(InputStream inputStream, String month) {
        List<InvoiceFinance> invoiceFinanceList = convertExcelToInvoice(inputStream, month);
        String invoiceIds = invoiceFinanceList.stream().map(invoice -> invoice.getInvoiceId()).collect(Collectors.joining(","));
        List<InvoiceFinance> invoiceFinances = getBaseMapper().findInvoiceIdIn(invoiceIds);

        List<String> existInvoiceIds = invoiceFinances.stream().map(invoice -> invoice.getInvoiceId()).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("validate", invoiceFinances.size() == 0);
        result.put("message", existInvoiceIds.toString());

        return result;
    }

    private RowHandler createRowHandler(List<InvoiceFinance> invoiceFinanceList, String month) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }
            if (StringUtils.isEmpty(convertString(rowlist.get(1)))) {
                throw new ServiceException("流水号为空，请检查导入模板");
            }


            InvoiceFinance invoiceFinance = new InvoiceFinance();

            invoiceFinance.setId(IdUtil.getSnowflakeNextIdStr());
            invoiceFinance.setInvoicingPeriod(month);
            invoiceFinance.setInvoicingDate(convertString(rowlist.get(0)));
            invoiceFinance.setOrgName(convertString(rowlist.get(1)));
            invoiceFinance.setInvoicingOrgName(convertString(rowlist.get(2)));
            invoiceFinance.setInvoiceBm(convertString(rowlist.get(3)));
            invoiceFinance.setInvoiceName(convertString(rowlist.get(4)));
            invoiceFinance.setInvoiceCode(convertString(rowlist.get(5)));
            invoiceFinance.setInvoiceId(convertString(rowlist.get(6)));
            invoiceFinance.setPayer(convertString(rowlist.get(7)));
            invoiceFinance.setSocialCreditCode(convertString(rowlist.get(8)));
            invoiceFinance.setPrinted(convertString(rowlist.get(9)));
            invoiceFinance.setRedInvoiceFlag(convertString(rowlist.get(10)));
            invoiceFinance.setPaperInvoiceNo(convertString(rowlist.get(11)));
            invoiceFinance.setElectronInvoiceNo(convertString(rowlist.get(12)));
            invoiceFinance.setCheckCode(convertString(rowlist.get(13)));
            invoiceFinance.setCreator(convertString(rowlist.get(14)));
            invoiceFinance.setItemCode(convertString(rowlist.get(15)));
            invoiceFinance.setItemName(convertString(rowlist.get(16)));
            invoiceFinance.setUnit(convertString(rowlist.get(17)));
            invoiceFinance.setQuantity(convertString(rowlist.get(18)));
            invoiceFinance.setStandard(convertString(rowlist.get(19)));
            invoiceFinance.setPrice(convertString(rowlist.get(20)));
            invoiceFinance.setRemark(convertString(rowlist.get(21)));
            invoiceFinance.setDataSplit(false);

            ValidateHelper.validData(invoiceFinance);

            invoiceFinanceList.add(invoiceFinance);

        };
    }

    public String convertString(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

}




