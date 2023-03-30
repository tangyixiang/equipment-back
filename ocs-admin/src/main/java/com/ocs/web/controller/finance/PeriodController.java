package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.FinancePeriodService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.helper.QueryHelper;
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
    public TableDataInfo list(FinancePeriod financePeriod) {
        startPage("create_time desc");
        QueryWrapper<FinancePeriod> wrapper = QueryHelper.dynamicCondition(financePeriod, CommonConstants.QUERY_EQUAL, false);
        List<FinancePeriod> list = financePeriodService.list(wrapper);
        return getDataTable(list);
    }

    @GetMapping("/listOpen")
    public TableDataInfo listOpen() {
        List<FinancePeriod> list = financePeriodService.listByMap(Map.of("del", CommonConstants.STATUS_NORMAL, "open", true));
        return getDataTable(list);
    }


    @GetMapping("/all")
    public Result listAll() {
        List<FinancePeriod> list = financePeriodService.lambdaQuery().eq(FinancePeriod::getDel, CommonConstants.STATUS_NORMAL)
                .orderByDesc(FinancePeriod::getCreateTime).list();
        return Result.success(list);
    }


    @PostMapping("/add")
    public Result add(@RequestBody @Validated FinancePeriod financePeriod) {
        FinancePeriod exist = financePeriodService.lambdaQuery().eq(FinancePeriod::getPeriod, financePeriod.getPeriod()).eq(FinancePeriod::getDel, CommonConstants.STATUS_NORMAL)
                .one();

        if (exist == null) {
            financePeriod.setOpen(false);
            financePeriod.setDel(CommonConstants.STATUS_NORMAL);
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

    @PostMapping("/status/change")
    public Result changePeriodStatus(@RequestBody @Validated FinancePeriod financePeriod) {
        if (financePeriod.getOpen()) {
            List<FinancePeriod> list = financePeriodService.lambdaQuery().eq(FinancePeriod::getOpen, true)
                    .eq(FinancePeriod::getDel, CommonConstants.STATUS_NORMAL).list();
            if (list.size() > 0) {
                throw new ServiceException("已存在打开的会计期, 请先关闭之前的, 只能同时打开一个会计期");
            }
        }
        update(financePeriod);
        return Result.success();
    }

    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<FinancePeriod>().deleteByIds(ids, financePeriodService);
        return Result.success();
    }

}
