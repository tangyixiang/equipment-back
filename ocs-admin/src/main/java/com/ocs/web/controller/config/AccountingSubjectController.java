package com.ocs.web.controller.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.domain.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/7
 */
@RestController
@RequestMapping("/accounting/subject")
public class AccountingSubjectController {

    @Autowired
    private AccountingSubjectService accountingSubjectService;

    @PostMapping("/add/type")
    public Result addType(@RequestBody AccountingSubject accountingSubject) {

        LambdaQueryWrapper<AccountingSubject> wrapper = new LambdaQueryWrapper<AccountingSubject>().eq(AccountingSubject::getParentId, "0");
        List<AccountingSubject> accountingSubjectList = accountingSubjectService.list(wrapper);
        Integer number = 0;
        if (accountingSubjectList.size() > 0) {
            accountingSubjectList.sort(Comparator.comparingInt(subject -> Integer.parseInt(subject.getId())));
            AccountingSubject subject = accountingSubjectList.get(accountingSubjectList.size() - 1);
            number = Integer.parseInt(subject.getId());
        }
        String id = StringUtils.leftPad(number + 1 + "", 6, "0");
        accountingSubject.setId(id);
        accountingSubject.setParentId("0");
        accountingSubject.setDel(CommonConstants.STATUS_NORMAL);
        accountingSubjectService.save(accountingSubject);
        return Result.success();
    }

}
