package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.entity.BankAccount;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.BankAccountService;
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
 * @Date 2022/11/4
 */
@RestController
@RequestMapping("/bank/account")
public class BankAccountController extends BaseController {

    @Autowired
    private BankAccountService bankAccountService;


    @GetMapping("/list")
    public TableDataInfo list() {
        startPage("create_time desc");
        QueryWrapper<BankAccount> queryWrapper = QueryHelper.dynamicCondition(new BankAccount(), CommonConstants.QUERY_EQUAL, false);
        List<BankAccount> list = bankAccountService.list(queryWrapper);
        return getDataTable(list);
    }


    @PostMapping("/add")
    public Result add(@RequestBody @Validated BankAccount bankAccount) {
        LambdaQueryWrapper<BankAccount> queryWrapper = new LambdaQueryWrapper<BankAccount>()
                .eq(BankAccount::getAccount, bankAccount.getAccount())
                .eq(BankAccount::getDel, CommonConstants.STATUS_NORMAL);
        List<BankAccount> list = bankAccountService.list(queryWrapper);
        if (list.size() > 0) {
            return Result.error("存在相同的账号");
        }
        bankAccount.setDel(CommonConstants.STATUS_NORMAL);
        bankAccountService.save(bankAccount);
        return Result.success();
    }


    @PostMapping("/update")
    public Result update(@RequestBody @Validated BankAccount bankAccount) {
        LambdaQueryWrapper<BankAccount> queryWrapper = new LambdaQueryWrapper<BankAccount>()
                .ne(BankAccount::getAccount, bankAccount.getAccount())
                .eq(BankAccount::getAccountName, bankAccount.getAccountName())
                .eq(BankAccount::getDel, CommonConstants.STATUS_NORMAL);

        List<BankAccount> list = bankAccountService.list(queryWrapper);
        if (list.size() > 0) {
            return Result.error("存在相同的账户名称");
        }
        bankAccountService.updateById(bankAccount);

        return Result.success();
    }


    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<BankAccount>().deleteByIds(ids, bankAccountService);
        return Result.success();
    }

}
