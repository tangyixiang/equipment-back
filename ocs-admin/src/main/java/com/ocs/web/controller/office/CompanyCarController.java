package com.ocs.web.controller.office;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.CompanyCar;
import com.ocs.busi.service.CompanyCarService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.helper.QueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangyixiang
 * @Date 2022/10/12
 */
@RestController
@RequestMapping("/office/car")
public class CompanyCarController extends BaseController {

    @Autowired
    private CompanyCarService companyCarService;


    @GetMapping("/list")
    public TableDataInfo list(CompanyCar companyCar) {
        startPage("create_time desc");
        QueryWrapper<CompanyCar> companyCarQueryWrapper = QueryHelper.dynamicCondition(companyCar, CommonConstants.QUERY_LIKE, false);
        List<CompanyCar> list = companyCarService.list(companyCarQueryWrapper);
        return getDataTable(list);
    }


    /**
     * 新增车辆管理
     *
     * @param companyCar 车辆信息
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody @Validated CompanyCar companyCar) {
        validateCar(companyCar);
        companyCar.setId(IdUtil.objectId());
        companyCar.setDel(CommonConstants.STATUS_NORMAL);
        boolean save = companyCarService.save(companyCar);
        if (save) {
            return Result.success();
        }
        return Result.error("保存失败");
    }

    /**
     * 更新车辆管理
     *
     * @param companyCar 车辆信息
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody @Validated CompanyCar companyCar) {
        validateCar(companyCar);
        boolean update = companyCarService.updateById(companyCar);
        if (update) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 删除车辆管理
     *
     * @param ids 车辆信息
     * @return
     */
    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        for (String id : ids) {
            CompanyCar companyCar = new CompanyCar();
            companyCar.setId(id);
            companyCar.setDel(CommonConstants.STATUS_DEL);
            companyCarService.updateById(companyCar);

        }
        return Result.success();
    }

    private void validateCar(CompanyCar companyCar) {
        LambdaQueryWrapper<CompanyCar> wrapper = new LambdaQueryWrapper<CompanyCar>()
                .eq(CompanyCar::getPlateNumber, companyCar.getPlateNumber()).eq(CompanyCar::getDel, CommonConstants.STATUS_NORMAL);
        List<CompanyCar> exist = companyCarService.list(wrapper);
        if (StringUtils.isNotEmpty(companyCar.getId())) {
            if (exist.size() == 0) {
                return;
            }
            // 自身修改信息
            List<CompanyCar> collect = exist.stream().filter(car -> car.getId().equals(companyCar.getId())).collect(Collectors.toList());
            if (collect.size() == 0) {
                throw new ServiceException("存在相同车辆");
            }
        } else {
            if (exist.size() > 0) {
                throw new ServiceException("存在相同车辆");
            }
        }
    }

}
