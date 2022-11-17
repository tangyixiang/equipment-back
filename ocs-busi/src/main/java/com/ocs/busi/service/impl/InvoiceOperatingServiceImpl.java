package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.InvoiceOperating;
import com.ocs.busi.domain.entity.InvoiceOperatingSplit;
import com.ocs.busi.helper.ExcelCellHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.InvoiceOperatingMapper;
import com.ocs.busi.service.InvoiceOperatingService;
import com.ocs.busi.service.InvoiceOperatingSplitService;
import com.ocs.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tangyx
 * @createDate 2022-11-07 11:58:55
 */
@Service
public class InvoiceOperatingServiceImpl extends ServiceImpl<InvoiceOperatingMapper, InvoiceOperating>
        implements InvoiceOperatingService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceOperatingServiceImpl.class);

    @Autowired
    private InvoiceOperatingSplitService invoiceOperatingSplitService;

    @Override
    @Transactional
    public synchronized void importInvoice(InputStream inputStream, String period) {
        List<InvoiceOperating> invoiceOperatingList = convertExcelToInvoice(inputStream, period);

        invoiceOperatingList.forEach(invoice -> {
            invoice.setId(IdUtil.objectId());
            InvoiceOperating oldData = getBaseMapper().findByFlowId(invoice.getFlowId());
            // 如果生成凭证,只保留最近3次的记录
            if (oldData != null && oldData.getDataSplit() == Boolean.TRUE) {
                // 保持原来的ID
                invoice.setId(oldData.getId());
                List<InvoiceOperatingSplit> invoiceOperatingSplitList = invoiceOperatingSplitService.list(new LambdaQueryWrapper<InvoiceOperatingSplit>()
                        .eq(InvoiceOperatingSplit::getInvoiceOperatingId, oldData.getId()));
                Map<String, List<InvoiceOperatingSplit>> taskIdGroupMap = invoiceOperatingSplitList.stream().collect(Collectors.groupingBy(InvoiceOperatingSplit::getTaskId));
                if (taskIdGroupMap.keySet().size() > 3) {
                    List<InvoiceOperatingSplit> taskFirstDataList = taskIdGroupMap.keySet().stream().map(k -> taskIdGroupMap.get(k).stream().findFirst().get()).collect(Collectors.toList());
                    List<InvoiceOperatingSplit> taskDataOrderList = taskFirstDataList.stream().sorted(Comparator.comparing(InvoiceOperatingSplit::getCreateTime).reversed()).collect(Collectors.toList());
                    for (int i = 3; i < taskDataOrderList.size(); i++) {
                        LambdaQueryWrapper<InvoiceOperatingSplit> wrapper = new LambdaQueryWrapper<InvoiceOperatingSplit>().eq(InvoiceOperatingSplit::getInvoiceOperatingId, oldData.getId())
                                .eq(InvoiceOperatingSplit::getTaskId, taskDataOrderList.get(i).getTaskId());
                        invoiceOperatingSplitService.remove(wrapper);
                    }
                }
            }
            saveOrUpdate(invoice);
        });
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
            invoiceOperating.setTotalPrice(convertString(rowlist.get(26)));
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
        return value == null ? null : String.valueOf(value);
    }

}




