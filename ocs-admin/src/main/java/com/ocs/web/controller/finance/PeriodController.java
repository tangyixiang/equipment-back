package com.ocs.web.controller.finance;

import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.FinancePeriodService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/period")
public class PeriodController extends BaseController {

    @Autowired
    private FinancePeriodService financePeriodService;

    @GetMapping("/list")
    public TableDataInfo list() {
        startPage("create_time desc");
        List<FinancePeriod> list = financePeriodService.listByMap(Map.of("del", CommonConstants.STATUS_NORMAL));
        return getDataTable(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody @Validated FinancePeriod financePeriod) {
        FinancePeriod exist = financePeriodService.lambdaQuery().eq(FinancePeriod::getPeriod, financePeriod.getPeriod()).eq(FinancePeriod::getDel, CommonConstants.STATUS_NORMAL)
                .eq(FinancePeriod::getType, financePeriod.getType()).one();

        if (exist == null) {
            financePeriodService.save(financePeriod);
        } else {
            throw new ServiceException("会计期间已存在");
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result update(@RequestBody @Validated FinancePeriod financePeriod) {
        financePeriodService.updateById(financePeriod);
        return Result.success();
    }

    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<FinancePeriod>().deleteByIds(ids, financePeriodService);
        return Result.success();
    }
}
