package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.AccountingSubject;
import com.ocs.busi.mapper.AccountingSubjectMapper;
import com.ocs.busi.service.AccountingSubjectService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.domain.entity.SysDictData;
import com.ocs.common.exception.ServiceException;
import com.ocs.system.service.ISysDictDataService;
import com.ocs.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangyx
 * @description 针对表【accounting_subject(会计科目)】的数据库操作Service实现
 * @createDate 2022-11-07 17:52:26
 */
@Service
public class AccountingSubjectServiceImpl extends ServiceImpl<AccountingSubjectMapper, AccountingSubject>
        implements AccountingSubjectService {

    @Autowired
    private ISysDictDataService dictDataService;
    @Autowired
    private ISysDictTypeService dictTypeService;

    @Override
    public AccountingSubject findFinanceItemValue(String itemName) {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictLabel(itemName);
        List<SysDictData> sysDictDataList = dictDataService.selectDictDataList(sysDictData);
        SysDictData dictData = sysDictDataList.stream().filter(data -> dictTypeService.selectDictTypeById(data.getDictCode()).getGroup().equals(CommonConstants.DICT_FINANCE_GROUP))
                .findFirst().get();

        LambdaQueryWrapper<AccountingSubject> wrapper = new LambdaQueryWrapper<AccountingSubject>().eq(AccountingSubject::getItemId, dictData.getDictValue());
        AccountingSubject subject = getOne(wrapper);
        return subject;
    }

    @Override
    public Map<String, String> findOperatingData(String itemName) {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictLabel(itemName);
        List<SysDictData> sysDictDataList = dictDataService.selectDictDataList(sysDictData);
        SysDictData dictData = sysDictDataList.stream().filter(data -> dictTypeService.selectDictTypeById(data.getDictCode()).getGroup().equals(CommonConstants.DICT_FINANCE_GROUP))
                .findFirst().get();
        LambdaQueryWrapper<AccountingSubject> wrapper = new LambdaQueryWrapper<AccountingSubject>().eq(AccountingSubject::getItemId, dictData.getDictValue());

        List<AccountingSubject> accountingSubjectList = list(wrapper);

        HashMap<String, String> map = new HashMap<>();
        accountingSubjectList.forEach(subject -> {
            SysDictData mappingDictData = findDictData(subject.getMappingId());
            map.put(mappingDictData.getDictLabel(), subject.getValue());
        });

        return map;
    }


    private SysDictData findDictData(String dictValue) {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictValue(dictValue);
        List<SysDictData> sysDictDataList = dictDataService.selectDictDataList(sysDictData);
        SysDictData dictData = sysDictDataList.stream().filter(data -> dictTypeService.selectDictTypeById(data.getDictCode()).getGroup().equals(CommonConstants.DICT_FINANCE_GROUP))
                .findFirst().get();
        if (dictData == null) {
            throw new ServiceException("未找到值:" + dictValue + "的字典");
        }
        return dictData;
    }
}




