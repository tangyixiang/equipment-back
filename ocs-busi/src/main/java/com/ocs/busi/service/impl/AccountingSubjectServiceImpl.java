package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.dto.AccountingSubjectDto;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.mapper.AccountingSubjectMapper;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.utils.StringUtils;
import com.ocs.common.utils.sql.QueryUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangyx
 * @description 针对表【accounting_subject(会计科目)】的数据库操作Service实现
 * @createDate 2022-11-07 17:52:26
 */
@Service
public class AccountingSubjectServiceImpl extends ServiceImpl<AccountingSubjectMapper, AccountingSubject>
        implements AccountingSubjectService {


    @Override
    public List<AccountingSubjectDto> getAccountSubjectTree() {
        QueryWrapper<AccountingSubject> wrapper = QueryUtil.dynamicCondition(new AccountingSubject(), CommonConstants.QUERY_EQUAL, false);
        return getSubjectDtoList(wrapper);
    }

    @Override
    public List<AccountingSubjectDto> findSubjectChild(String name) {
        QueryWrapper<AccountingSubject> wrapper = QueryUtil.dynamicCondition(new AccountingSubject(), CommonConstants.QUERY_EQUAL, false);
        wrapper.eq("name",name);
        return getSubjectDtoList(wrapper);
    }

    private List<AccountingSubjectDto> getSubjectDtoList(QueryWrapper<AccountingSubject> wrapper) {
        List<AccountingSubject> allSubject = list(wrapper);
        List<AccountingSubject> topSubjectList = allSubject.stream().filter(subject -> subject.getParentId().equals("0")).collect(Collectors.toList());
        if (topSubjectList.size() == 0) {
            return buildTree(topSubjectList, allSubject);
        } else {
            return Collections.emptyList();
        }
    }


    private List<AccountingSubjectDto> buildTree(List<AccountingSubject> topSubjectList, List<AccountingSubject> allSubject) {
        List<AccountingSubjectDto> tree = new ArrayList<>();
        for (AccountingSubject accountingSubject : topSubjectList) {
            AccountingSubjectDto treeNode = new AccountingSubjectDto();
            treeNode.setSubject(accountingSubject);
            if (StringUtils.isNotEmpty(accountingSubject.getParentId())) {
                List<AccountingSubject> subjectList = allSubject.stream().filter(subject -> subject.getParentId().equals(accountingSubject.getId())).collect(Collectors.toList());
                treeNode.setChild(buildTree(subjectList, allSubject));
            }
            tree.add(treeNode);
        }
        return tree;
    }
}




