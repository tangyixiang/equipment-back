package com.ocs.busi.service.impl;

import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.ocs.busi.helper.ValidateHelper;
import com.ocs.busi.mapper.CompanyClientOrgMapper;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import com.ocs.common.helper.QueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CompanyClientOrgServiceImpl extends ServiceImpl<CompanyClientOrgMapper, CompanyClientOrg>
        implements CompanyClientOrgService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyClientOrgServiceImpl.class);

    @Override
    @Transactional
    public void importOrg(InputStream inputStream) {
        List<CompanyClientOrg> companyClientOrgList = new ArrayList<>();
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(companyClientOrgList));
        try {
            reader.read(inputStream, "0");
        } catch (POIException | IllegalArgumentException e) {
            logger.error("客户信息导入失败:{}", e);
            throw new ServiceException("模板异常,请上传正确的导入模板");
        }

        companyClientOrgList.forEach(org -> {
            CompanyClientOrg companyClientOrg = getById(org.getId());
            if (companyClientOrg != null && !companyClientOrg.getName().equals(org.getName())) {
                throw new ServiceException("客户ID:" + org.getId() + ", 不允许修改客户名称");
            }
            org.setDel(CommonConstants.STATUS_NORMAL);
            saveOrUpdate(org);
        });

    }

    @Override
    public List<CompanyClientOrg> findAllCompanyOrg() {
        QueryWrapper<CompanyClientOrg> queryWrapper = QueryHelper.dynamicCondition(new CompanyClientOrg(), CommonConstants.QUERY_EQUAL, false);
        List<CompanyClientOrg> clientOrgList = list(queryWrapper);
        return clientOrgList;
    }


    private RowHandler createRowHandler(List<CompanyClientOrg> companyClientOrgList) {
        AtomicInteger row = new AtomicInteger();
        return (sheetIndex, rowIndex, rowlist) -> {
            if (row.incrementAndGet() <= 1 || rowlist.stream().allMatch(cell -> cell == null || StringUtils.isEmpty(String.valueOf(cell)))) {
                return;
            }
            if (StringUtils.isEmpty((String) rowlist.get(0))) {
                throw new ServiceException("客户ID为空，请检查导入模板");
            }
            CompanyClientOrg org = new CompanyClientOrg();
            org.setId((String) rowlist.get(0));
            org.setName((String) rowlist.get(1));
            org.setSocialCreditCode((String) rowlist.get(2));
            org.setAddress((String) rowlist.get(3));
            org.setRegion((String) rowlist.get(4));

            ValidateHelper.validData(org);

            companyClientOrgList.add(org);
        };
    }
}




