package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.EmployeeSalary;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.EmployeeSalaryMapper;
import com.ocs.busi.service.EmployeeSalaryService;
import com.ocs.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangyx
 * @description 针对表【employee_salary(员工工资)】的数据库操作Service实现
 * @createDate 2022-11-07 11:58:55
 */
@Service
public class EmployeeSalaryServiceImpl extends ServiceImpl<EmployeeSalaryMapper, EmployeeSalary>
        implements EmployeeSalaryService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSalaryServiceImpl.class);

    @Override
    @Transactional
    public synchronized void importSalary(InputStream inputStream, String period) {
        List<EmployeeSalary> employeeSalaryList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(employeeSalaryList, period));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("工资导入失败:{}", e);
            throw new ServiceException("模板数据异常,请上传正确的导入模板");
        }

        if (employeeSalaryList.size() == 0) {
            throw new ServiceException("未找到工资区间符合" + period + "期间的数据");
        }
        employeeSalaryList.forEach(invoice -> {
            invoice.setId(IdUtil.objectId());
            saveOrUpdate(invoice);
        });
    }

    private RowHandler createRowHandler(List<EmployeeSalary> employeeSalaryList, String period) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }

            if (period.equals(rowlist.get(0) + "") || StringUtils.isEmpty(period)) {
                EmployeeSalary employeeSalary = new EmployeeSalary();

                employeeSalary.setSalaryPeriod(rowlist.get(0) + "");
                employeeSalary.setEmployeeCode(rowlist.get(1) + "");
                employeeSalary.setEmployeeName(rowlist.get(2) + "");
                employeeSalary.setPostSalary(covertDouble(rowlist.get(3) + ""));
                employeeSalary.setRankSalary(covertDouble(rowlist.get(4) + ""));
                employeeSalary.setPerformanceSalary(covertDouble(rowlist.get(5) + ""));
                employeeSalary.setHousingSalary(covertDouble(rowlist.get(6) + ""));
                employeeSalary.setBonusSalary(covertDouble(rowlist.get(7) + ""));
                employeeSalary.setExtraIncreaseSalary1(covertDouble(rowlist.get(8) + ""));
                employeeSalary.setExtraIncreaseSalary2(covertDouble(rowlist.get(9) + ""));
                employeeSalary.setExtraIncreaseSalary3(covertDouble(rowlist.get(10) + ""));
                employeeSalary.setExtraIncreaseSalary4(covertDouble(rowlist.get(11) + ""));
                employeeSalary.setExtraIncreaseSalary5(covertDouble(rowlist.get(12) + ""));
                employeeSalary.setPaymentRatio(convertString(rowlist.get(13)));
                employeeSalary.setUnPaymentSalary(covertDouble(rowlist.get(14) + ""));
                employeeSalary.setSalaryPayable(covertDouble(rowlist.get(15) + ""));
                employeeSalary.setEndowmentInsurance(covertDouble(rowlist.get(16) + ""));
                employeeSalary.setMedicalInsurance(covertDouble(rowlist.get(17) + ""));
                employeeSalary.setUnemploymentInsurance(covertDouble(rowlist.get(18) + ""));
                employeeSalary.setHousingAccumulationFunds(covertDouble(rowlist.get(19) + ""));
                employeeSalary.setOccupationalAnnuity(covertDouble(rowlist.get(20) + ""));
                employeeSalary.setOccupationalAnnuityDifferent(covertDouble(rowlist.get(21) + ""));
                employeeSalary.setUnionFees(covertDouble(rowlist.get(22) + ""));
                employeeSalary.setIndividualIncomeTax(covertDouble(rowlist.get(23) + ""));
                employeeSalary.setExtraDecreaseSalary1(covertDouble(rowlist.get(24) + ""));
                employeeSalary.setExtraDecreaseSalary2(covertDouble(rowlist.get(25) + ""));
                employeeSalary.setExtraDecreaseSalary3(covertDouble(rowlist.get(26) + ""));
                employeeSalary.setExtraDecreaseSalary4(covertDouble(rowlist.get(27) + ""));
                employeeSalary.setExtraDecreaseSalary5(covertDouble(rowlist.get(28) + ""));
                employeeSalary.setDecreaseTotalSalary(covertDouble(rowlist.get(29) + ""));
                employeeSalary.setActualAmount(covertDouble(rowlist.get(30) + ""));
                employeeSalary.setRemark(convertString(rowlist.get(31)));

                ValidateHelper.validData(employeeSalary);

                employeeSalaryList.add(employeeSalary);
            }
        };
    }


    private Double covertDouble(String value) {
        return StringUtils.isEmpty(value) || value.equals("null") ? null : Double.parseDouble(value);
    }

    public String convertString(Object value){
        return value == null ? null : String.valueOf(value);
    }
}




