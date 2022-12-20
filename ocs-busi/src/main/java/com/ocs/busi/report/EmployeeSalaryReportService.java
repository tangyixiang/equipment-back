package com.ocs.busi.report;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.dto.EmployeeSalaryReportDto;
import com.ocs.busi.domain.entity.EmployeeSalary;
import com.ocs.busi.domain.model.EmployeeSalaryGroupModel;
import com.ocs.busi.domain.vo.EmployeeSalaryReportVo;
import com.ocs.busi.service.EmployeeSalaryService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EmployeeSalaryReportService {

    @Autowired
    private EmployeeSalaryService employeeSalaryService;


    public List<EmployeeSalaryReportVo> statistics(List<String> conditions, EmployeeSalaryReportDto employeeSalaryReportDto) {

        LambdaQueryWrapper<EmployeeSalary> wrapper = new LambdaQueryWrapper<EmployeeSalary>().ge(EmployeeSalary::getSalaryPeriod, employeeSalaryReportDto.getStartPeriod())
                .le(EmployeeSalary::getSalaryPeriod, employeeSalaryReportDto.getEndPeriod());
        // 获取所有的数据
        List<EmployeeSalary> list = employeeSalaryService.list(wrapper);
        // 数据分组
        List<EmployeeSalaryGroupModel> groupModelList = groupByCondition(list, conditions, conditions.get(0));

        List<EmployeeSalaryReportVo> reportVoList = convertToVo(groupModelList);

        return reportVoList;

    }


    private List<EmployeeSalaryReportVo> convertToVo(List<EmployeeSalaryGroupModel> modelList) {
        List<EmployeeSalaryReportVo> reportVoList = new ArrayList<>();
        for (EmployeeSalaryGroupModel employeeSalaryGroupModel : modelList) {
            EmployeeSalaryReportVo vo = new EmployeeSalaryReportVo();
            vo.setName(employeeSalaryGroupModel.getName());
            vo.setType(employeeSalaryGroupModel.getType());
            List<EmployeeSalary> list = employeeSalaryGroupModel.getList();

            Optional<EmployeeSalary> EmployeeSalaryTotalOptional = summaryEmployeeSalary(list);

            if (EmployeeSalaryTotalOptional.isPresent()) {
                BeanUtils.copyProperties(EmployeeSalaryTotalOptional.get(), vo);
                vo.setId(IdUtil.fastSimpleUUID());
                // vo.setEmployeeSalary(EmployeeSalaryTotalOptional.get());
            }

            if (ObjectUtils.isNotEmpty(employeeSalaryGroupModel.getChildModel())) {
                vo.setChildren(convertToVo(employeeSalaryGroupModel.getChildModel()));
            }

            reportVoList.add(vo);
        }
        return reportVoList;
    }

    private static Optional<EmployeeSalary> summaryEmployeeSalary(List<EmployeeSalary> list) {
        Optional<EmployeeSalary> EmployeeSalaryTotalOptional = list.stream().reduce((a, b) -> new EmployeeSalary(
                a.getPostSalary() + b.getPostSalary(),
                a.getAdditionPostSalary() + b.getAdditionPostSalary(),
                a.getRankSalary() + b.getRankSalary(),
                a.getAdditionRankSalary() + b.getAdditionRankSalary(),
                a.getPerformanceSalary() + b.getPerformanceSalary(),
                a.getAdditionPerformanceSalary() + b.getAdditionPerformanceSalary(),
                a.getExtraIncreaseSalary1() + b.getExtraIncreaseSalary1(),
                a.getExtraIncreaseSalary2() + b.getExtraIncreaseSalary2(),
                a.getExtraIncreaseSalary3() + b.getExtraIncreaseSalary3(),
                a.getExtraIncreaseSalary4() + b.getExtraIncreaseSalary4(),
                a.getExtraIncreaseSalary5() + b.getExtraIncreaseSalary5(),
                a.getSalaryTotal() + b.getSalaryTotal(),
                a.getBonusSalary() + b.getBonusSalary(),
                a.getHousingSalary() + b.getBonusSalary(),
                a.getSalaryPayable() + b.getRankSalary(),
                a.getEndowmentInsurance() + b.getEndowmentInsurance(),
                a.getMedicalInsurance() + b.getMedicalInsurance(),
                a.getUnemploymentInsurance() + b.getMedicalInsurance(),
                a.getHousingAccumulationFunds() + b.getHousingAccumulationFunds(),
                a.getUnionFees() + b.getUnionFees(),
                a.getOccupationalAnnuity() + b.getOccupationalAnnuity(),
                a.getExtraDecreaseSalary1() + b.getExtraDecreaseSalary1(),
                a.getExtraDecreaseSalary2() + b.getExtraDecreaseSalary2(),
                a.getExtraDecreaseSalary3() + b.getExtraDecreaseSalary3(),
                a.getExtraDecreaseSalary4() + b.getExtraDecreaseSalary4(),
                a.getExtraDecreaseSalary5() + b.getExtraDecreaseSalary5(),
                a.getDecreaseTotalSalary() + b.getDecreaseTotalSalary(),
                a.getIndividualIncomeTax() + b.getIndividualIncomeTax(),
                a.getActualAmount() + b.getActualAmount()
        ));
        return EmployeeSalaryTotalOptional;
    }


    private List<EmployeeSalaryGroupModel> groupByCondition(List<EmployeeSalary> list, List<String> conditions, String type) {
        List<EmployeeSalaryGroupModel> modelList = new ArrayList<>();
        Map<Object, List<EmployeeSalary>> collect = null;
        if (type.equals("period")) {
            collect = list.stream().collect(Collectors.groupingBy(EmployeeSalary::getSalaryPeriod));
        }

        if (type.equals("dept")) {
            collect = list.stream().collect(Collectors.groupingBy(EmployeeSalary::getEmployeeDept));
        }

        if (type.equals("employeeType")) {
            collect = list.stream().collect(Collectors.groupingBy(EmployeeSalary::getEmployeeType));
        }

        if (type.equals("employeeName")) {
            collect = list.stream().collect(Collectors.groupingBy(EmployeeSalary::getEmployeeName));
        }

        for (Object key : collect.keySet()) {
            EmployeeSalaryGroupModel deptGroupModel = new EmployeeSalaryGroupModel();
            deptGroupModel.setName(key + "");
            deptGroupModel.setType(type);
            deptGroupModel.setList(collect.get(key));
            int index = conditions.indexOf(type);
            if (index != -1 && index + 1 < conditions.size()) {
                deptGroupModel.setChildModel(groupByCondition(collect.get(key), conditions, conditions.get(index + 1)));
            }

            modelList.add(deptGroupModel);
        }

        return modelList;
    }

}
