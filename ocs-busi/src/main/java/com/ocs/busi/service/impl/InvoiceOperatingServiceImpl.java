package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.entity.InvoiceOperating;
import com.ocs.busi.helper.ExcelCellHelper;
import com.ocs.busi.helper.InvoiceHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.InvoiceOperatingMapper;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.busi.service.InvoiceOperatingService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author tangyx
 * @createDate 2022-11-07 11:58:55
 */
@Service
public class InvoiceOperatingServiceImpl extends ServiceImpl<InvoiceOperatingMapper, InvoiceOperating>
        implements InvoiceOperatingService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceOperatingServiceImpl.class);

    @Value("${busi.config.bankAccount.operate}")
    private String bankAccount;

    @Autowired
    private InvoiceHelper invoiceHelper;
    @Autowired
    private CompanyReceivablesService receivablesService;

    @Override
    @Transactional
    public synchronized void importInvoice(InputStream inputStream, String period) {
        List<InvoiceOperating> invoiceOperatingList = convertExcelToInvoice(inputStream, period);
        // 校验数据
        invoiceHelper.validateOperate(invoiceOperatingList);

        delRepeatData(invoiceOperatingList);

        // 保存发票
        saveBatch(invoiceOperatingList);
        saveReceivable(invoiceOperatingList, period);
    }

    private void saveReceivable(List<InvoiceOperating> invoiceOperatingList, String period) {
        List<CompanyReceivables> receivablesList = new ArrayList<>();
        // 冲正的发票
        List<String> redList = invoiceOperatingList.stream().filter(invoiceOperating -> invoiceOperating.getRedInvoice().trim().equals("红票"))
                .map(invoiceOperating -> {
                    String remark = invoiceOperating.getRemark();
                    Pattern pattern = Pattern.compile("(?<=号码:)[0-9]+");
                    Matcher matcher = pattern.matcher(remark);
                    String number = "";
                    while (matcher.find()) {
                        number = matcher.group();
                    }
                    return number;
                }).collect(Collectors.toList());

        for (InvoiceOperating invoice : invoiceOperatingList) {
            CompanyReceivables receivables = new CompanyReceivables();
            receivables.setId(invoice.getId());
            receivables.setPeriod(invoice.getInvoicingPeriod());
            receivables.setSourceType(CommonConstants.RECEIVABLE_OPERATE);
            receivables.setBankAccount(bankAccount);
            receivables.setInvoicingDate(invoice.getInvoicingTime().toLocalDate());
            receivables.setClientOrgName(invoice.getBuyerName());
            receivables.setReceivableAmount(Double.parseDouble(invoice.getTotalPriceIncludingTax()));
            receivables.setUnConfirmAmount(Double.parseDouble(invoice.getTotalPriceIncludingTax()));
            receivables.setReconciliationFlag(CommonConstants.NOT_RECONCILED);
            receivables.setValid(invoice.getInvoiceState().trim().equals("开票完成") && !redList.contains(invoice.getInvoiceId()));
            receivables.setInvoiceId(invoice.getInvoiceId());
            receivables.setDirection(invoice.getRedInvoice().trim().equals("蓝票") ? CommonConstants.INVOICE_DIRECT_FORWARD : CommonConstants.INVOICE_DIRECT_REVERSE);
            receivablesList.add(receivables);
        }

        // 应收账单
        receivablesService.saveBatch(receivablesList);
    }

    private void delRepeatData(List<InvoiceOperating> invoiceOperatingList) {
        for (InvoiceOperating invoice : invoiceOperatingList) {
            Wrapper<InvoiceOperating> wrapper = new LambdaQueryWrapper<InvoiceOperating>().eq(InvoiceOperating::getInvoiceId, invoice.getInvoiceId());
            List<InvoiceOperating> list = list(wrapper);
            if (list.size() > 0) {
                logger.info("经营性发票重复导入,删除之前的,发票号码:{}", invoice.getInvoiceId());
                //TODO 后续再完善删除逻辑
                remove(wrapper);
                // 删除应收账单
                receivablesService.removeBatchByIds(list.stream().map(InvoiceOperating::getId).collect(Collectors.toList()));
            }
        }
    }

    private List<InvoiceOperating> convertExcelToInvoice(InputStream inputStream, String period) {
        List<InvoiceOperating> invoiceOperatingList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(invoiceOperatingList, period));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("经营发票导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板");
        }
        return invoiceOperatingList;
    }

    @Override
    public List<String> monthPeriod(String month) {
        return getBaseMapper().groupByInvoicingPeriod();
    }

    @Override
    public Map<String, Object> uploadValidate(InputStream inputStream, String period) {
        List<InvoiceOperating> invoiceOperatingList = convertExcelToInvoice(inputStream, period);

        // 校验数据
        invoiceHelper.validateOperate(invoiceOperatingList);

        String flowIds = invoiceOperatingList.stream().map(invoice -> invoice.getFlowId()).collect(Collectors.joining(","));
        List<InvoiceOperating> invoiceOperatings = getBaseMapper().findFlowIdIn(flowIds);

        List<String> existInvoiceIds = invoiceOperatings.stream().map(invoice -> invoice.getFlowId()).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("validate", invoiceOperatings.size() == 0);
        result.put("message", existInvoiceIds.toString());

        return result;
    }


    private RowHandler createRowHandler(List<InvoiceOperating> invoiceOperatingList, String period) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }
            if (StringUtils.isEmpty(convertString(rowlist.get(1)))) {
                throw new ServiceException("流水号为空，请检查导入模板");
            }

            InvoiceOperating invoiceOperating = new InvoiceOperating();

            invoiceOperating.setId(IdUtil.getSnowflakeNextIdStr());

            invoiceOperating.setFlowId(convertString(rowlist.get(0)));
            invoiceOperating.setOrderId(convertString(rowlist.get(1)));
            invoiceOperating.setInvoiceCreateTime(ExcelCellHelper.handleDate(rowlist.get(2) + ""));
            invoiceOperating.setInvoicingTime(ExcelCellHelper.handleDate(rowlist.get(3) + ""));
            // 开票期间
            invoiceOperating.setInvoicingPeriod(period);

            invoiceOperating.setRedInvoice(convertString(rowlist.get(4)));
            invoiceOperating.setInvoiceType(convertString(rowlist.get(5)));
            invoiceOperating.setInvoiceCode(convertString(rowlist.get(6)));
            invoiceOperating.setInvoiceId(convertString(rowlist.get(7)));
            invoiceOperating.setBuyerName(convertString(rowlist.get(8)));
            invoiceOperating.setBuyerTaxId(convertString(rowlist.get(9)));
            invoiceOperating.setBuyerPhone(convertString(rowlist.get(10)));
            invoiceOperating.setBuyerEmail(convertString(rowlist.get(11)));
            invoiceOperating.setBuyerBankInfo(convertString(rowlist.get(12)));
            invoiceOperating.setBuyerAddressInfo(convertString(rowlist.get(13)));
            invoiceOperating.setProductName(convertString(rowlist.get(14)));
            invoiceOperating.setProductCode(convertString(rowlist.get(15)));
            invoiceOperating.setSpecifications(convertString(rowlist.get(16)));
            invoiceOperating.setUnit(convertString(rowlist.get(17)));
            invoiceOperating.setQuantity(convertString(rowlist.get(18)));
            invoiceOperating.setUnitPriceIncludingTax(convertString(rowlist.get(19)));
            invoiceOperating.setTaxRate(convertString(rowlist.get(20)));
            invoiceOperating.setPriceIncludingTax(convertString(rowlist.get(21)));
            invoiceOperating.setPriceExcludingTax(convertString(rowlist.get(22)));
            invoiceOperating.setTaxPrice(convertString(rowlist.get(23)));
            invoiceOperating.setTotalPriceIncludingTax(convertString(rowlist.get(24)));
            invoiceOperating.setTotalPriceExcludingTax(convertString(rowlist.get(25)));
            invoiceOperating.setTotalTaxPrice(convertString(rowlist.get(26)));
            invoiceOperating.setRemark(convertString(rowlist.get(27)));
            invoiceOperating.setBillingStaff(convertString(rowlist.get(28)));
            invoiceOperating.setPayee(convertString(rowlist.get(29)));
            invoiceOperating.setReviewer(convertString(rowlist.get(30)));
            invoiceOperating.setStore(convertString(rowlist.get(31)));
            invoiceOperating.setInvoicingMethod(convertString(rowlist.get(32)));
            invoiceOperating.setPdfPath(convertString(rowlist.get(33)));
            invoiceOperating.setInvoiceState(convertString(rowlist.get(34)));
            invoiceOperating.setSpecialInvoice(convertString(rowlist.get(35)));
            invoiceOperating.setClearFlag(convertString(rowlist.get(36)));
            invoiceOperating.setExtCode(convertString(rowlist.get(37)));
            invoiceOperating.setMachineCode(convertString(rowlist.get(38)));
            invoiceOperating.setTerminalCode(convertString(rowlist.get(39)));
            invoiceOperating.setCheckCode(convertString(rowlist.get(40)));
            invoiceOperating.setUnitPriceExcludingTax(convertString(rowlist.get(41)));
            invoiceOperating.setInvalidInfo(convertString(rowlist.get(42)));
            invoiceOperating.setReceiverPhone(convertString(rowlist.get(43)));
            invoiceOperating.setReceiverEmail(convertString(rowlist.get(44)));
            invoiceOperating.setCarInfo(convertString(rowlist.get(45)));
            invoiceOperating.setAuctionName(convertString(rowlist.get(46)));
            invoiceOperating.setAuctionAddress(convertString(rowlist.get(47)));
            invoiceOperating.setAuctionTaxNo(convertString(rowlist.get(48)));
            invoiceOperating.setAuctionPhone(convertString(rowlist.get(49)));
            invoiceOperating.setBankInfo(convertString(rowlist.get(50)));
            invoiceOperating.setDataSplit(false);

            ValidateHelper.validData(invoiceOperating);

            invoiceOperatingList.add(invoiceOperating);
        };
    }


    /*private String getInvoicingPeriod(LocalDateTime dateTime) {
        int monthValue = dateTime.getMonthValue();
        String month = StringUtils.leftPad(monthValue , 2, "0");
        return dateTime.getYear() + month ;
    }*/

    public String convertString(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

}




