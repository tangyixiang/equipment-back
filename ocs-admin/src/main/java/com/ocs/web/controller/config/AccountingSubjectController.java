package com.ocs.web.controller.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.helper.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/7
 */
@RestController
@RequestMapping("/accounting/subject")
public class AccountingSubjectController extends BaseController {

    @Autowired
    private AccountingSubjectService accountingSubjectService;

    @PostMapping("/add")
    public Result add(@RequestBody @Validated AccountingSubject accountingSubject) {
        accountingSubject.setDel(CommonConstants.STATUS_NORMAL);
        accountingSubjectService.save(accountingSubject);
        return Result.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(AccountingSubject accountingSubject) {
        startPage();
        QueryWrapper<AccountingSubject> wrapper = QueryHelper.dynamicCondition(accountingSubject, CommonConstants.QUERY_LIKE, false);
        List<AccountingSubject> list = accountingSubjectService.list(wrapper);
        return getDataTable(list);
    }

    @PostMapping("/update")
    public Result update(@RequestBody AccountingSubject accountingSubject) {
        accountingSubjectService.updateById(accountingSubject);
        return Result.success();
    }


    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<AccountingSubject>().deleteByIds(ids, accountingSubjectService);
        return Result.success();
    }


}
