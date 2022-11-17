package com.ocs.web.controller.examine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ocs.busi.domain.dto.CompanyContractAddDto;
import com.ocs.busi.domain.dto.CompanyContractDto;
import com.ocs.busi.domain.entity.CompanyContract;
import com.ocs.busi.service.CompanyContractService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.sql.QueryUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/19
 */
@RestController
@RequestMapping("/contract")
public class CompanyContractController extends BaseController {


    @Autowired
    private CompanyContractService companyContractService;


    @GetMapping("/list")
    public TableDataInfo list(CompanyContractDto companyContractDto) {
        startPage("create_time desc");
        CompanyContract contract = new CompanyContract();
        BeanUtils.copyProperties(companyContractDto, contract);
        QueryWrapper<CompanyContract> queryWrapper = QueryUtil.dynamicCondition(contract, CommonConstants.QUERY_LIKE, false);
        if (ObjectUtils.isNotEmpty(companyContractDto.getContractSignDateEnd())) {
            queryWrapper.between("start_date", companyContractDto.getContractSignDateStart(), companyContractDto.getContractSignDateEnd());
        }
        List<CompanyContract> list = companyContractService.list(queryWrapper);
        return getDataTable(list);
    }


    @PostMapping("/add")
    public Result add(@RequestBody @Validated CompanyContractAddDto companyContractAddDto) {
        LambdaQueryWrapper<CompanyContract> queryWrapper = new LambdaQueryWrapper<CompanyContract>()
                .eq(CompanyContract::getName, companyContractAddDto.getContract().getName())
                .eq(CompanyContract::getStatus, CommonConstants.STATUS_NORMAL);
        List<CompanyContract> list = companyContractService.list(queryWrapper);
        if (list.size() > 0) {
            return Result.error("存在相同的已生效合同名称");
        }

        companyContractService.save(companyContractAddDto);

        return Result.success();
    }


    @PostMapping("/update")
    public Result update(@RequestBody @Validated CompanyContractAddDto companyContractAddDto) {
        LambdaQueryWrapper<CompanyContract> queryWrapper = new LambdaQueryWrapper<CompanyContract>()
                .ne(CompanyContract::getId, companyContractAddDto.getContract().getId())
                .eq(CompanyContract::getName, companyContractAddDto.getContract().getName())
                .eq(CompanyContract::getStatus, CommonConstants.STATUS_NORMAL);
        List<CompanyContract> list = companyContractService.list(queryWrapper);
        if (list.size() > 0) {
            return Result.error("存在相同的已生效合同名称");
        }
        companyContractService.updateContract(companyContractAddDto);

        return Result.success("更新成功");
    }


    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        for (String id : ids) {
            LambdaUpdateWrapper<CompanyContract> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(CompanyContract::getId, id);
            wrapper.set(CompanyContract::getDel, CommonConstants.STATUS_DEL);
            companyContractService.update(null, wrapper);
        }
        return Result.success();
    }


}
