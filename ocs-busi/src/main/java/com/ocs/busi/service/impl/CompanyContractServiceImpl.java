package com.ocs.busi.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.dto.CompanyContractAddDto;
import com.ocs.busi.domain.entity.CompanyContract;
import com.ocs.busi.domain.entity.CompanyContractItem;
import com.ocs.busi.domain.entity.CompanyContractOrg;
import com.ocs.busi.mapper.CompanyContractMapper;
import com.ocs.busi.service.CompanyContractItemService;
import com.ocs.busi.service.CompanyContractOrgService;
import com.ocs.busi.service.CompanyContractService;
import com.ocs.common.constant.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class CompanyContractServiceImpl extends ServiceImpl<CompanyContractMapper, CompanyContract>
        implements CompanyContractService {

    @Autowired
    private CompanyContractItemService companyContractItemService;
    @Autowired
    private CompanyContractOrgService companyContractOrgService;

    @Override
    @Transactional
    public void save(CompanyContractAddDto companyContractAddDto) {
        CompanyContract contract = companyContractAddDto.getContract();
        CompanyContractOrg contractOrg = companyContractAddDto.getContractOrg();
        List<CompanyContractItem> items = companyContractAddDto.getItems();

        String contractId = IdUtil.objectId();
        contract.setId(contractId);
        contract.setContractCode(createContractCode());
        contractOrg.setContractId(contractId);
        contractOrg.setDel(CommonConstants.STATUS_NORMAL);
        items.forEach(item -> {
            item.setId(IdUtil.objectId());
            item.setContractId(contractId);
            item.setDel(CommonConstants.STATUS_NORMAL);
        });

        save(contract);
        companyContractOrgService.save(contractOrg);
        companyContractItemService.saveBatch(items);

    }

    @Override
    @Transactional
    public void updateContract(CompanyContractAddDto companyContractAddDto) {
        CompanyContract contract = companyContractAddDto.getContract();
        CompanyContractOrg contractOrg = companyContractAddDto.getContractOrg();
        List<CompanyContractItem> items = companyContractAddDto.getItems();

        items.forEach(item -> {
            if (StringUtils.isEmpty(item.getId())) {
                item.setId(IdUtil.objectId());
            }
            if (StringUtils.isEmpty(item.getContractId())) {
                item.setContractId(contract.getId());
            }
            if (StringUtils.isEmpty(item.getDel())) {
                item.setDel(CommonConstants.STATUS_NORMAL);
            }
        });

        save(contract);
        companyContractOrgService.save(contractOrg);
        companyContractItemService.saveBatch(items);
    }


    private String createContractCode() {
        QueryWrapper<CompanyContract> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        wrapper.last("limit 1");

        CompanyContract companyContract = getOne(wrapper);
        String template = "GXTJ-C/C-%d-%d";
        Integer code = 1;
        if (companyContract != null) {
            String[] strings = companyContract.getContractCode().split("-");
            String year = strings[strings.length - 2];
            String tempCode = strings[strings.length - 1];
            if (year.equals(LocalDate.now().getYear() + "")) {
                code = Integer.parseInt(tempCode) + 1;
            }
        }
        return String.format(template, LocalDate.now().getYear(), StringUtils.leftPad(code + "", 4, "0"));
    }


}




