package com.ocs.busi.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.ocs.busi.domain.entity.CompanyEmployee;
import com.ocs.busi.domain.entity.InvoiceFinance;
import com.ocs.busi.domain.entity.InvoiceOperating;
import com.ocs.busi.mapper.CompanyEmployeeMapper;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InvoiceHelper {

    @Autowired
    private CompanyClientOrgService clientOrgService;
    @Autowired
    private CompanyEmployeeMapper companyEmployeeMapper;

    /*
     * “购方名称”需要与系统的“客户名称”校验，
     * “开票员”、“收款人”、“复核人”需要跟系统的“职员”校验。
     */
    public void validateOperate(List<InvoiceOperating> list) {

        list.parallelStream().forEach(invoiceOperating -> {
            String buyerName = invoiceOperating.getBuyerName();
            String billingStaff = invoiceOperating.getBillingStaff();
            String payee = invoiceOperating.getPayee();
            String reviewer = invoiceOperating.getReviewer();

            List<CompanyClientOrg> clientOrgList = clientOrgService.list(new LambdaQueryWrapper<CompanyClientOrg>().eq(CompanyClientOrg::getName, buyerName)
                    .eq(CompanyClientOrg::getDel, CommonConstants.STATUS_NORMAL));
            if (clientOrgList.size() == 0) {
                throw new ServiceException("购方名称:" + buyerName + ",不存在");
            }

            List.of(billingStaff, payee, reviewer).stream().forEach(name -> {
                validateEmployee(name);
            });
        });
    }


    /*
     * 缴款人”需要与系统的“客户名称”校验，“社会信用统一代码”需要跟系统的客户资料里的“社会信用统一代码”校验，“编制人”需要跟“职员”校验。
     */
    public void validateFinance(List<InvoiceFinance> list) {

        list.parallelStream().forEach(invoiceFinance -> {
            String payer = invoiceFinance.getPayer();
            String socialCreditCode = invoiceFinance.getSocialCreditCode();
            String creator = invoiceFinance.getCreator();

            List<CompanyClientOrg> clientOrgList = clientOrgService.list(new LambdaQueryWrapper<CompanyClientOrg>().eq(CompanyClientOrg::getName, payer)
                    .eq(CompanyClientOrg::getDel, CommonConstants.STATUS_NORMAL));
            if (clientOrgList.size() == 0) {
                throw new ServiceException("缴款人:" + payer + ",不存在");
            }
            Optional<CompanyClientOrg> clientOrg = clientOrgList.stream().filter(org -> org.getSocialCreditCode().equals(socialCreditCode)).findAny();
            if (!clientOrg.isPresent()) {
                throw new ServiceException("缴款人:" + payer + ",社会信用统一代码与系统录入的不匹配");
            }

            validateEmployee(creator);
        });
    }

    private void validateEmployee(String name) {
        List<CompanyEmployee> employeeList = companyEmployeeMapper.findByNickName(name);
        if (employeeList.size() == 0) {
            throw new ServiceException("职员:" + name + ", 不存在");
        }
        employeeList.stream().filter(employee -> employee.getStatus().equals("0")).findAny().orElseThrow(() -> new ServiceException("职员:" + name + ", 被禁用"));
    }


}
