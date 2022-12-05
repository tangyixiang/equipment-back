package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.domain.entity.InvoiceFinanceSplit;
import com.ocs.busi.helper.InvoiceHelper;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.InvoiceFinanceMapper;
import com.ocs.busi.service.InvoiceFinanceService;
import com.ocs.busi.service.InvoiceFinanceSplitService;
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
public class InvoiceFinanceServiceImpl extends ServiceImpl<InvoiceFinanceMapper, InvoiceFinance>
        implements InvoiceFinanceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceFinanceServiceImpl.class);

    @Autowired
    private InvoiceFinanceSplitService invoiceFinanceSplitService;
    @Autowired
    private InvoiceHelper invoiceHelper;

    @Override
    @Transactional
    public synchronized void importInvoice(InputStream inputStream, String period) {
        List<InvoiceFinance> invoiceFinanceList = convertExcelToInvoice(inputStream, period);
        // 数据校验
        invoiceHelper.validateFinance(invoiceFinanceList);

        invoiceFinanceList.forEach(invoice -> {
            invoice.setId(IdUtil.objectId());
            InvoiceFinance oldData = getBaseMapper().findByInvoiceId(invoice.getInvoiceId());
            // 如果生成凭证,只保留最近3次的记录
            if (oldData != null && oldData.getDataSplit() == Boolean.TRUE) {
                // 保持原来的ID
                invoice.setId(oldData.getId());
                // 执行了的任务次数
                List<InvoiceFinanceSplit> invoiceFinanceSplitList = invoiceFinanceSplitService.list(new LambdaQueryWrapper<InvoiceFinanceSplit>()
                        .eq(InvoiceFinanceSplit::getInvoiceFinanceId, oldData.getId()));
                Map<String, List<InvoiceFinanceSplit>> taskIdGroupMap = invoiceFinanceSplitList.stream().collect(Collectors.groupingBy(InvoiceFinanceSplit::getTaskId));

                if (taskIdGroupMap.keySet().size() > 3) {
                    List<InvoiceFinanceSplit> taskFirstDataList = taskIdGroupMap.keySet().stream().map(k -> taskIdGroupMap.get(k).stream().findFirst().get()).collect(Collectors.toList());
                    List<InvoiceFinanceSplit> taskDataOrderList = taskFirstDataList.stream().sorted(Comparator.comparing(InvoiceFinanceSplit::getCreateTime).reversed()).collect(Collectors.toList());
                    for (int i = 3; i < taskDataOrderList.size(); i++) {
                        LambdaQueryWrapper<InvoiceFinanceSplit> wrapper = new LambdaQueryWrapper<InvoiceFinanceSplit>().eq(InvoiceFinanceSplit::getInvoiceFinanceId, oldData.getId())
                                .eq(InvoiceFinanceSplit::getTaskId, taskDataOrderList.get(i).getTaskId());
                        invoiceFinanceSplitService.remove(wrapper);
                    }
                }
            }
            saveOrUpdate(invoice);
        });

        // 删除这个期间
        remove(new LambdaQueryWrapper<InvoiceFinance>().eq(InvoiceFinance::getInvoicingPeriod, period));

        saveOrUpdateBatch(invoiceFinanceList);

    }

    private List<InvoiceFinance> convertExcelToInvoice(InputStream inputStream, String month) {
        List<InvoiceFinance> invoiceFinanceList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(invoiceFinanceList, month));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("财政发票导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板");
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
        return value == null ? null : String.valueOf(value);
    }

}




