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

    public List<EmployeeSalaryReportVo> statistics(EmployeeSalaryReportDto employeeSalaryReportDto) {
        List<EmployeeSalaryGroupModel> topModelList = new ArrayList<>();

        LambdaQueryWrapper<EmployeeSalary> wrapper = new LambdaQueryWrapper<EmployeeSalary>().ge(EmployeeSalary::getSalaryPeriod, employeeSalaryReportDto.getStartPeriod())
                .le(EmployeeSalary::getSalaryPeriod, employeeSalaryReportDto.getEndPeriod());
        // 获取所有的数据
        List<EmployeeSalary> list = employeeSalaryService.list(wrapper);

        // 根据期间分
        if (employeeSalaryReportDto.isPeriodCondition()) {
            topModelList = groupByCondition(list, "period");
        }
        // 根据部门分
        if (employeeSalaryReportDto.isDeptCondition()) {
            // 期间划分有值
            if (topModelList.size() != 0) {
                for (EmployeeSalaryGroupModel employeeSalaryGroupModel : topModelList) {
                    List<EmployeeSalary> periodSalaryList = employeeSalaryGroupModel.getList();
                    List<EmployeeSalaryGroupModel> deptModelList = groupByCondition(periodSalaryList, "dept");
                    employeeSalaryGroupModel.setChildModel(deptModelList);
                }
            } else {
                topModelList = groupByCondition(list, "dept");
            }
        }

        // 根据员工类型分
        if (employeeSalaryReportDto.isEmployeeTypeCondition()) {
            // 期间划分有值
            if (topModelList.size() != 0) {
                for (EmployeeSalaryGroupModel employeeSalaryGroupModel : topModelList) {
                    // 说明按部门划分了
                    if (ObjectUtils.isNotEmpty(employeeSalaryGroupModel.getChildModel())) {
                        for (EmployeeSalaryGroupModel salaryGroupModel : employeeSalaryGroupModel.getChildModel()) {
                            List<EmployeeSalaryGroupModel> groupModelList = groupByCondition(salaryGroupModel.getList(), "employeeType");
                            salaryGroupModel.setChildModel(groupModelList);
                        }
                    } else {
                        List<EmployeeSalaryGroupModel> groupModelList = groupByCondition(employeeSalaryGroupModel.getList(), "employeeType");
                        employeeSalaryGroupModel.setChildModel(groupModelList);
                    }
                }
            } else {
                topModelList = groupByCondition(list, "employeeType");
            }
        }

        List<EmployeeSalaryReportVo> reportVoList = convertToVo(topModelList);

        return reportVoList;

    }

    private List<EmployeeSalaryReportVo> convertToVo(List<EmployeeSalaryGroupModel> modelList) {
        List<EmployeeSalaryReportVo> reportVoList = new ArrayList<>();
        for (EmployeeSalaryGroupModel employeeSalaryGroupModel : modelList) {
            EmployeeSalaryReportVo vo = new EmployeeSalaryReportVo();
            vo.setName(employeeSalaryGroupModel.getName());
            vo.setType(employeeSalaryGroupModel.getType());
            List<EmployeeSalary> list = employeeSalaryGroupModel.getList();

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


    private List<EmployeeSalaryGroupModel> groupByCondition(List<EmployeeSalary> list, String type) {
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

        for (Object key : collect.keySet()) {
            EmployeeSalaryGroupModel deptGroupModel = new EmployeeSalaryGroupModel();
            deptGroupModel.setName(key + "");
            deptGroupModel.setType(type);
            deptGroupModel.setList(collect.get(key));
            modelList.add(deptGroupModel);
        }

        return modelList;
    }

}
