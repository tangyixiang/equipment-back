package com.ocs.busi.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocs.busi.domain.entity.BankFlow;
import com.ocs.busi.domain.entity.CompanyReceivables;
import com.ocs.busi.domain.entity.FinancePeriod;
import com.ocs.busi.domain.model.ReceivableBankFlowMapping;
import com.ocs.busi.service.*;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.exception.ServiceException;
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
    @Autowired
    private BankFlowLogService bankFlowLogService;
    @Autowired
    private FinancePeriodService financePeriodService;
    // 账期
    private String period;

    List<String> notReconciled = List.of(CommonConstants.NOT_RECONCILED, CommonConstants.PART_RECONCILED);

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
    public void reconciliationTask(String period) {
        logger.info("对账任务开始运行");

        FinancePeriod financePeriod = financePeriodService.lambdaQuery().eq(FinancePeriod::getPeriod, period).one();
        if (financePeriod == null) {
            throw new ServiceException("会计期间不存在");
        }
        this.period = financePeriod.getPeriod();
        logger.info("对账账期:{}", period);

        String today = DateUtil.format(new Date(), "yyyyMMdd");
        // 未对账的应收单
        List<CompanyReceivables> receivablesList = companyReceivablesService.lambdaQuery().eq(CompanyReceivables::getValid, true)
                .eq(CompanyReceivables::getDirection, CommonConstants.INVOICE_DIRECT_FORWARD)
                .in(CompanyReceivables::getReconciliationFlag, notReconciled).list();

        // 精准对账
        logger.info("银行流水金额 == 应收单金额对账开始");
        oneBankFlowMatch(today, receivablesList, "equal");
        logger.info("银行流水金额 == 应收单金额对账结束");
        logger.info("==================================================");
        logger.info("银行流水金额 > 应收单金额对账开始");
        oneBankFlowMatch(today, receivablesList, "gt");
        logger.info("银行流水金额 > 应收单金额对账结束");
        logger.info("==================================================");
        logger.info("银行流水金额 < 应收单金额对账开始");
        multiBankFlowMatch(today, CommonConstants.AUTO_RECONCILIATION, receivablesList, null);
        logger.info("银行流水金额 < 应收单金额对账结束");
    }

    /**
     * 单笔银行流水能覆盖单笔应收账单
     */
    public void oneBankFlowMatch(String today, List<CompanyReceivables> receivablesList, String type) {
        Map<String, List<CompanyReceivables>> receivablesGroupMap = receivablesList.stream().collect(Collectors.groupingBy(CompanyReceivables::getClientOrgName));
        for (String clientOrgName : receivablesGroupMap.keySet()) {
            logger.info("客户:{},开始自动对账", clientOrgName);

            List<BankFlow> bankFlowList = bankFlowService.lambdaQuery().in(BankFlow::getReconciliationFlag, notReconciled)
                    .eq(BankFlow::getAdversaryOrgName, clientOrgName).eq(BankFlow::getTradeType, CommonConstants.LOAN).orderByAsc(BankFlow::getCreateTime)
                    .list();
            List<CompanyReceivables> companyReceivablesList = receivablesGroupMap.get(clientOrgName);
            companyReceivablesList.stream().sorted(Comparator.comparing(CompanyReceivables::getInvoicingDate));

            bankFlowMatch(today, CommonConstants.AUTO_RECONCILIATION, bankFlowList, companyReceivablesList, type);
        }
    }


    public void bankFlowMatch(String today, String reconciliationModel, List<BankFlow> bankFlowList, List<CompanyReceivables> companyReceivablesList, String type) {
        // 过滤已经对账的数据
        List<CompanyReceivables> filterReceivables = filterReceivables(companyReceivablesList);
        for (CompanyReceivables companyReceivables : filterReceivables) {
            // 过滤银行账号
            List<BankFlow> filterBankFLows = filterBankFLow(bankFlowList, companyReceivables.getBankAccount());
            // 筛选银行流水数据
            Optional<BankFlow> bankFlowOptional = type.equals("equal") ?
                    filterBankFLows.stream().filter(flow -> new BigDecimal(flow.getUnConfirmPrice()).compareTo(new BigDecimal(companyReceivables.getUnConfirmAmount())) == 0).findFirst()
                    : filterBankFLows.stream().filter(flow -> new BigDecimal(flow.getUnConfirmPrice()).compareTo(new BigDecimal(companyReceivables.getUnConfirmAmount())) > 0).findFirst();

            if (bankFlowOptional.isPresent()) {
                logger.info("金额匹配模式,{},应收单金额:{},银行流水金额:{}", type, companyReceivables.getUnConfirmAmount(), bankFlowOptional.get().getUnConfirmPrice());
                // 对账ID
                String dzId = "ZD-" + today + "/" + RandomStringUtils.randomNumeric(5);

                BankFlow bankFlow = bankFlowOptional.get();
                // 已对账金额
                bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + companyReceivables.getUnConfirmAmount());
                bankFlow.setUnConfirmPrice(bankFlow.getUnConfirmPrice() - companyReceivables.getUnConfirmAmount());
                // 已对账
                bankFlow.setReconciliationFlag(bankFlow.getUnConfirmPrice().equals(0d) ? CommonConstants.RECONCILED : CommonConstants.PART_RECONCILED);
                // 自动对账
                bankFlow.setReconciliationModel(reconciliationModel);
                bankFlow.getAssociationId().add(dzId);
                // 对账单
                companyReceivables.setReconciliationFlag(CommonConstants.RECONCILED);
                companyReceivables.setReconciliationModel(reconciliationModel);
                companyReceivables.setConfirmAmount(companyReceivables.getConfirmAmount() + companyReceivables.getUnConfirmAmount());
                // 记录使用了这笔银行流水的金额
                companyReceivables.getRemark().add(new ReceivableBankFlowMapping(bankFlow.getId(), companyReceivables.getUnConfirmAmount()));
                companyReceivables.setUnConfirmAmount(0d);

                // 添加日志
                bankFlowLogService.addBankFlowUseLog(bankFlow, companyReceivables, companyReceivables.getUnConfirmAmount(), period);

                bankFlowService.updateById(bankFlow);
            }
        }
        companyReceivablesService.updateBatchById(companyReceivablesList);
    }

    /**
     * 多笔银行流水覆盖一笔应收账单
     */
    public void multiBankFlowMatch(String today, String reconciliationModel, List<CompanyReceivables> receivablesList, List<BankFlow> bankFlowList) {
        List<CompanyReceivables> filterReceivables = filterReceivables(receivablesList);
        Map<String, List<CompanyReceivables>> receivablesGroupMap = filterReceivables.stream().collect(Collectors.groupingBy(CompanyReceivables::getClientOrgName));

        for (String clientOrgName : receivablesGroupMap.keySet()) {
            logger.info("客户:{},开始对账-多笔流水", clientOrgName);

            if (ObjectUtils.isEmpty(bankFlowList)) {
                LambdaQueryWrapper<BankFlow> bankFlowWrapper = new LambdaQueryWrapper<BankFlow>().in(BankFlow::getReconciliationFlag, notReconciled)
                        .eq(BankFlow::getAdversaryOrgName, clientOrgName).eq(BankFlow::getTradeType, CommonConstants.LOAN);
                bankFlowList = bankFlowService.list(bankFlowWrapper);
            }
            List<CompanyReceivables> list = receivablesGroupMap.get(clientOrgName);
            list.stream().sorted(Comparator.comparing(CompanyReceivables::getInvoicingDate));

            for (CompanyReceivables companyReceivables : list) {
                // 对账ID
                String dzId = "ZD-" + today + "/" + RandomStringUtils.randomNumeric(5);
                Double unConfirmAmount = companyReceivables.getUnConfirmAmount();
                double multiBankFlowAmount = 0d;
                double diff = 0d;

                logger.info("部分对账,应收单金额:{}", unConfirmAmount);
                logger.info("多笔银行流水金额匹配开始");
                List<BankFlow> filterBankFLows = filterBankFLow(bankFlowList, companyReceivables.getBankAccount());
                if (filterBankFLows.size() == 0) {
                    logger.info("未找到符合条件的流水");
                    continue;
                }
                // 多笔流水去匹配
                for (BankFlow bankFlow : filterBankFLows) {
                    double lastMultiBankFlowAmount = multiBankFlowAmount;
                    multiBankFlowAmount = multiBankFlowAmount + bankFlow.getUnConfirmPrice();
                    diff = new BigDecimal(unConfirmAmount).subtract(new BigDecimal(multiBankFlowAmount)).doubleValue();
                    logger.info("部分对账,多笔银行流水金额:{},diff:{}", multiBankFlowAmount, diff);
                    bankFlow.setReconciliationFlag(CommonConstants.RECONCILED);
                    bankFlow.setReconciliationModel(reconciliationModel);
                    bankFlow.getAssociationId().add(dzId);

                    if (diff < 0) {
                        double useUnConfirmAmount = unConfirmAmount - lastMultiBankFlowAmount;
                        // 记录使用了这笔银行流水的金额
                        companyReceivables.getRemark().add(new ReceivableBankFlowMapping(bankFlow.getId(), useUnConfirmAmount));
                        bankFlow.setUnConfirmPrice(bankFlow.getUnConfirmPrice() - useUnConfirmAmount);
                        bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + useUnConfirmAmount);
                        bankFlow.setReconciliationFlag(CommonConstants.PART_RECONCILED);
                        // 添加日志
                        bankFlowLogService.addBankFlowUseLog(bankFlow, companyReceivables, useUnConfirmAmount, period);
                        break;
                    } else if (diff == 0) {
                        // 如果差值相等，则用掉所有余额
                        companyReceivables.getRemark().add(new ReceivableBankFlowMapping(bankFlow.getId(), bankFlow.getUnConfirmPrice()));
                        bankFlow.setUnConfirmPrice(0d);
                        bankFlow.setConfirmPrice(bankFlow.getPrice());
                        bankFlow.setReconciliationFlag(CommonConstants.RECONCILED);
                        // 添加日志
                        bankFlowLogService.addBankFlowUseLog(bankFlow, companyReceivables, bankFlow.getUnConfirmPrice(), period);
                        break;
                    } else {
                        companyReceivables.getRemark().add(new ReceivableBankFlowMapping(bankFlow.getId(), bankFlow.getUnConfirmPrice()));
                        bankFlow.setConfirmPrice(bankFlow.getConfirmPrice() + bankFlow.getUnConfirmPrice());
                        bankFlow.setUnConfirmPrice(0d);
                        // 添加日志
                        bankFlowLogService.addBankFlowUseLog(bankFlow, companyReceivables, bankFlow.getUnConfirmPrice(), period);
                    }
                }
                logger.info("多笔银行流水金额匹配结束");
                // 说明所有的流水都无法完全覆盖
                if (diff > 0) {
                    companyReceivables.setConfirmAmount(companyReceivables.getReceivableAmount() - diff);
                    companyReceivables.setUnConfirmAmount(diff);
                } else {
                    companyReceivables.setConfirmAmount(companyReceivables.getConfirmAmount() + companyReceivables.getUnConfirmAmount());
                    companyReceivables.setUnConfirmAmount(0d);
                }
                companyReceivables.setReconciliationModel(reconciliationModel);
                companyReceivables.setReconciliationFlag(diff > 0 ? CommonConstants.PART_RECONCILED : CommonConstants.RECONCILED);
                bankFlowService.updateBatchById(bankFlowList);
            }
            companyReceivablesService.updateBatchById(list);
        }
    }

    /**
     * 过滤已对账的
     *
     * @param list
     * @return
     */
    private List<CompanyReceivables> filterReceivables(List<CompanyReceivables> list) {
        return list.stream().filter(companyReceivables -> !companyReceivables.getReconciliationFlag().equals(CommonConstants.RECONCILED)).collect(Collectors.toList());
    }

    /**
     * 过滤已对账的、账号相同的
     *
     * @param list
     * @param bankAccount
     * @return
     */
    private List<BankFlow> filterBankFLow(List<BankFlow> list, String bankAccount) {
        return list.stream().filter(flow -> !flow.getReconciliationFlag().equals(CommonConstants.RECONCILED)).filter(flow -> flow.getSelfAccount().equals(bankAccount)).collect(Collectors.toList());
    }

}
