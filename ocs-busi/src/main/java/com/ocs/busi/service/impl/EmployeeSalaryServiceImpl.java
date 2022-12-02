package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyEmployee;
import com.ocs.busi.domain.entity.EmployeeSalary;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.CompanyEmployeeMapper;
import com.ocs.busi.mapper.EmployeeSalaryMapper;
import com.ocs.busi.service.EmployeeSalaryService;
import com.ocs.common.core.domain.entity.SysDept;
import com.ocs.common.exception.ServiceException;
import com.ocs.system.service.ISysDeptService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class EmployeeSalaryServiceImpl extends ServiceImpl<EmployeeSalaryMapper, EmployeeSalary> implements EmployeeSalaryService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSalaryServiceImpl.class);

    @Autowired
    private CompanyEmployeeMapper companyEmployeeMapper;
    @Autowired
    private ISysDeptService sysDeptService;

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
        employeeSalaryList.parallelStream().forEach(employeeSalary -> {
            List<CompanyEmployee> employeeList = companyEmployeeMapper.findByNickName(employeeSalary.getEmployeeName());
            if (employeeList.size() == 0) {
                throw new ServiceException("职员:" + employeeSalary.getEmployeeName() + ", 不存在");
            }
            SysDept sysDept = new SysDept();
            sysDept.setDeptName(employeeSalary.getEmployeeDept());
            List<SysDept> depts = sysDeptService.selectDeptList(sysDept);
            if (depts.size() == 0) {
                throw new ServiceException("部门:" + employeeSalary.getEmployeeDept() + ", 不存在");
            }
        });


        LambdaQueryWrapper<EmployeeSalary> delWrapper = new LambdaQueryWrapper<EmployeeSalary>().eq(EmployeeSalary::getSalaryPeriod, period);
        // 删除旧数据
        boolean remove = remove(delWrapper);
        if (remove) {
            logger.info("工资导入删除旧的工资期间数据,工资期间:{}", period);
        }

        employeeSalaryList.forEach(invoice -> {
            invoice.setId(IdUtil.objectId());
            saveOrUpdate(invoice);
        });
    }

    private RowHandler createRowHandler(List<EmployeeSalary> employeeSalaryList, String period) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 2 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }

            if (period.equals(rowlist.get(1) + "") || StringUtils.isEmpty(period)) {
                EmployeeSalary employeeSalary = new EmployeeSalary();

                employeeSalary.setSalaryPeriod(rowlist.get(1) + "");
                employeeSalary.setEmployeeName(rowlist.get(2) + "");
                employeeSalary.setEmployeeDept(rowlist.get(3) + "");
                employeeSalary.setEmployeeAccountNo(rowlist.get(4) + "");
                employeeSalary.setEmployeeIdNumber(rowlist.get(5) + "");
                employeeSalary.setPostSalary(covertDouble(rowlist.get(6) + ""));
                employeeSalary.setAdditionPostSalary(covertDouble(rowlist.get(7) + ""));
                employeeSalary.setRankSalary(covertDouble(rowlist.get(8) + ""));
                employeeSalary.setAdditionRankSalary(covertDouble(rowlist.get(9) + ""));
                employeeSalary.setPerformanceSalary(covertDouble(rowlist.get(10) + ""));
                employeeSalary.setAdditionPerformanceSalary(covertDouble(rowlist.get(11) + ""));
                employeeSalary.setExtraIncreaseSalary1(covertDouble(rowlist.get(12) + ""));
                employeeSalary.setExtraIncreaseSalary2(covertDouble(rowlist.get(13) + ""));
                employeeSalary.setExtraIncreaseSalary3(covertDouble(rowlist.get(14) + ""));
                employeeSalary.setExtraIncreaseSalary4(covertDouble(rowlist.get(15) + ""));
                employeeSalary.setExtraIncreaseSalary5(covertDouble(rowlist.get(16) + ""));
                employeeSalary.setSalaryTotal(covertDouble(rowlist.get(17) + ""));
                employeeSalary.setBonusSalary(covertDouble(rowlist.get(18) + ""));
                employeeSalary.setHousingSalary(covertDouble(rowlist.get(19) + ""));
                employeeSalary.setSalaryPayable(covertDouble(rowlist.get(20) + ""));
                employeeSalary.setEndowmentInsurance(covertDouble(rowlist.get(21) + ""));
                employeeSalary.setMedicalInsurance(covertDouble(rowlist.get(22) + ""));
                employeeSalary.setUnemploymentInsurance(covertDouble(rowlist.get(23) + ""));
                employeeSalary.setHousingAccumulationFunds(covertDouble(rowlist.get(24) + ""));
                employeeSalary.setUnionFees(covertDouble(rowlist.get(25) + ""));
                employeeSalary.setOccupationalAnnuity(covertDouble(rowlist.get(26) + ""));
                employeeSalary.setExtraDecreaseSalary1(covertDouble(rowlist.get(27) + ""));
                employeeSalary.setExtraDecreaseSalary2(covertDouble(rowlist.get(28) + ""));
                employeeSalary.setExtraDecreaseSalary3(covertDouble(rowlist.get(29) + ""));
                employeeSalary.setExtraDecreaseSalary4(covertDouble(rowlist.get(30) + ""));
                employeeSalary.setExtraDecreaseSalary5(covertDouble(rowlist.get(31) + ""));
                employeeSalary.setDecreaseTotalSalary(covertDouble(rowlist.get(32) + ""));
                employeeSalary.setIndividualIncomeTax(covertDouble(rowlist.get(33) + ""));
                employeeSalary.setActualAmount(covertDouble(rowlist.get(34) + ""));
                employeeSalary.setRemark(rowlist.get(35) + "");

                ValidateHelper.validData(employeeSalary);

                employeeSalaryList.add(employeeSalary);
            }
        };
    }


    private Double covertDouble(String value) {
        return StringUtils.isEmpty(value) || value.equals("null") ? null : Double.parseDouble(value);
    }

}




