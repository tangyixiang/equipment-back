package com.ocs.busi.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.service.BankFlowService;
import com.ocs.busi.service.CompanyReceivablesService;
import com.ocs.common.constant.CommonConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 银行流水对账Task
 */
@Component
public class FlowTask {

    private static final Logger logger = LoggerFactory.getLogger(FlowTask.class);

    @Autowired
    private CompanyReceivablesService companyReceivablesService;
    @Autowired
    private BankFlowService bankFlowService;

    List<String> notReconciled = List.of(CommonConstants.NOT_RECONCILED, CommonConstants.PART_RECONCILED);

    LambdaQueryWrapper<CompanyReceivables> receivablesWrapper = new LambdaQueryWrapper<CompanyReceivables>().in(CompanyReceivables::getReconciliationFlag, notReconciled);

    /*
     * 自动对账规则：
     * 在系统“财务”—“应收对账管理”中，将所有的经营发票、财政发票和初始化应收单汇总了，统称为“应收单”。
     * 步骤一：先按客户和金额进行精确匹配，选中一张应收单后，去匹配跟这张应收单相同的客户，且金额相同的一张银行流水单进行匹配对账，
     * 且将这些单据的“对账标识”更新“Y”，对账类别为“自动对账”，且将应收单与相匹配的银行流水赋予同一个对账组ID（即应收对账ID）。
     * 步骤二，步骤二中针对步骤一未能匹配的应收单，对相同客户下未匹配的银行流水进行汇总，并与应收单金额进行比较，如汇总的银行流水金额大于等于应收单金额，
     * 则对银行流水清单进行顺序匹配，最后一单银行流水“对账标识”更新为“P”，“对账标识”为“P”的银行流水单剩余未对账金额以后可继续进行对账。
     * 若汇总的流水金额小于应收单金额，则对应收单进行顺序匹配，最后一单应收单“对账标识”更新为“P”，“对账标识”为“P”的应收单剩余未对账金额以后可继续进行对账。。
     * 以上两种场景，对账类别，对账组ID（即应收对账ID）更新规则与步骤一一致，对账标识”为“P”的应收单或银行流水最终对账完成后会有两个“对账组ID”
     * 剩余的各种特殊情况，比如“客户名称不全、别名”等需要人为判断的应收单和银行流水单，则只能进行手工对账。
     *
     * */

    /**
     * 对账任务运行
     */
    @Transactional
    public void reconciliationTask() {
        logger.info("对账任务开始运行");
        CompanyReceivables current = new CompanyReceivables();
        // 精准对账
        logger.info("银行流水金额 >= 应收单金额对账开始");
        priceExactMatch();
        logger.info("银行流水金额 >= 应收单金额对账结束");
        logger.info("银行流水金额 < 应收单金额对账开始");
        pricePartMatch();
        logger.info("银行流水金额 < 应收单金额对账结束");

    }

    /**
     * 单笔银行流水能覆盖单笔应收账单
     */
    public void priceExactMatch() {
        // 未对账的应收单
        List<CompanyReceivables> receivablesList = companyReceivablesService.list(receivablesWrapper);
        String today = DateUtil.format(new Date(), "yyyyMMdd");

        Map<String, List<CompanyReceivables>> receivablesGroupMap = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getClientOrgName));

        for (String clientOrgName : receivablesGroupMap.keySet()) {
            logger.info("客户:{},开始自动对账", clientOrgName);

            LambdaQueryWrapper<BankFlow> bankFlowWrapper = new LambdaQueryWrapper<BankFlow>().in(BankFlow::getReconciliationFlag, notReconciled).eq(BankFlow::getAdversaryOrgName, clientOrgName);
            List<BankFlow> bankFlowList = bankFlowService.list(bankFlowWrapper);
            List<CompanyReceivables> list = receivablesGroupMap.get(clientOrgName);
            list.stream().sorted(Comparator.comparing(CompanyReceivables::getInvoicingDate));

            bankFlowMatch(today, CommonConstants.AUTO_RECONCILIATION, bankFlowList, list);
        }
    }

    @Transactional
    public void bankFlowMatch(String today, String reconciliationModel, List<BankFlow> bankFlowList, List<CompanyReceivables> list) {
        for (CompanyReceivables companyReceivables : list) {
            Optional<BankFlow> bankFlowOptional = bankFlowList.stream().filter(flow ->
                    (flow.getReconciliationFlag().equals(CommonConstants.NOT_RECONCILED) || flow.getReconciliationFlag().equals(CommonConstants.PART_RECONCILED))
                            && new BigDecimal(flow.getUnConfirmPrice()).compareTo(new BigDecimal(companyReceivables.getUnConfirmAmount())) > -1).findFirst();
            if (bankFlowOptional.isPresent()) {
                // 对账ID
                String dzId = "ZD-" + today + "/" + RandomStringUtils.randomNumeric(5);

                BankFlow bankFlow = bankFlowOptional.get();
                bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + companyReceivables.getUnConfirmAmount());
                bankFlow.setUnConfirmPrice(bankFlow.getUnConfirmPrice() - companyReceivables.getUnConfirmAmount());
                // 已对账
                bankFlow.setReconciliationFlag(bankFlow.getUnConfirmPrice().equals(0d) ? CommonConstants.RECONCILED : CommonConstants.PART_RECONCILED);
                // 自动对账
                bankFlow.setReconciliationModel(reconciliationModel);
                bankFlow.setAssociationId(addAssociationId(dzId, bankFlow.getAssociationId()));
                // 对账单
                companyReceivables.setReconciliationFlag(CommonConstants.RECONCILED);
                companyReceivables.setReconciliationModel(reconciliationModel);
                companyReceivables.setConfirmAmount(companyReceivables.getConfirmAmount() + companyReceivables.getUnConfirmAmount());
                companyReceivables.setAssociationId(addAssociationId(dzId, companyReceivables.getAssociationId()));
                bankFlowService.updateById(bankFlow);
            }
        }
        companyReceivablesService.updateBatchById(list);
    }

    /**
     * 多笔银行流水覆盖一笔应收账单
     */
    public void pricePartMatch() {
        // 未对账的应收单
        List<CompanyReceivables> receivablesList = companyReceivablesService.list(receivablesWrapper);
        String today = DateUtil.format(new Date(), "yyyyMMdd");

        Map<String, List<CompanyReceivables>> receivablesGroupMap = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getClientOrgName));

        for (String clientOrgName : receivablesGroupMap.keySet()) {
            logger.info("客户:{},开始自动对账", clientOrgName);

            LambdaQueryWrapper<BankFlow> bankFlowWrapper = new LambdaQueryWrapper<BankFlow>().in(BankFlow::getReconciliationFlag, notReconciled).eq(BankFlow::getAdversaryOrgName, clientOrgName);

            List<BankFlow> bankFlowList = bankFlowService.list(bankFlowWrapper);
            List<CompanyReceivables> list = receivablesGroupMap.get(clientOrgName);
            list.stream().sorted(Comparator.comparing(CompanyReceivables::getInvoicingDate));

            for (CompanyReceivables companyReceivables : list) {
                // 对账ID
                String dzId = "ZD-" + today + "/" + RandomStringUtils.randomNumeric(5);
                Double unConfirmAmount = companyReceivables.getUnConfirmAmount();
                double multiBankFlowAmount = 0d;
                double diff = 0d;
                // 多笔流水去匹配
                for (BankFlow bankFlow : bankFlowList) {
                    multiBankFlowAmount = multiBankFlowAmount + bankFlow.getUnConfirmPrice();
                    diff = new BigDecimal(unConfirmAmount).divide(new BigDecimal(multiBankFlowAmount)).doubleValue();

                    bankFlow.setReconciliationFlag(CommonConstants.RECONCILED);
                    bankFlow.setReconciliationModel(CommonConstants.AUTO_RECONCILIATION);
                    bankFlow.setAssociationId(addAssociationId(dzId, bankFlow.getAssociationId()));

                    if (diff <= 0) {
                        bankFlow.setUnConfirmPrice(bankFlow.getUnConfirmPrice() - Math.abs(diff));
                        bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + Math.abs(diff));
                        bankFlow.setReconciliationFlag(bankFlow.getUnConfirmPrice().equals(0d) ? CommonConstants.RECONCILED : CommonConstants.PART_RECONCILED);
                        break;
                    } else {
                        bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + bankFlow.getUnConfirmPrice());
                        bankFlow.setUnConfirmPrice(0d);
                    }
                }
                // 说明所有的流水都无法完全覆盖
                if (diff > 0) {
                    companyReceivables.setConfirmAmount(companyReceivables.getReceivableAmount() - diff);
                    companyReceivables.setUnConfirmAmount(diff);
                } else {
                    companyReceivables.setConfirmAmount(companyReceivables.getConfirmAmount() + companyReceivables.getUnConfirmAmount());
                    companyReceivables.setUnConfirmAmount(0d);
                }
                companyReceivables.setReconciliationModel(CommonConstants.AUTO_RECONCILIATION);
                companyReceivables.setReconciliationFlag(diff > 0 ? CommonConstants.PART_RECONCILED : CommonConstants.RECONCILED);
                companyReceivables.setAssociationId(addAssociationId(dzId, companyReceivables.getAssociationId()));
                bankFlowService.updateBatchById(bankFlowList);
            }
            companyReceivablesService.updateBatchById(list);
        }
    }

    /**
     * 添加新的对账ID
     *
     * @param dzId
     * @param originList
     * @return
     */
    private List<String> addAssociationId(String dzId, List<String> originList) {
        List<String> ids = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(originList)) {
            ids.addAll(originList);
        }
        ids.add(dzId);
        return ids;
    }

}
