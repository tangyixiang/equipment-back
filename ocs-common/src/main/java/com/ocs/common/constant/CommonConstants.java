package com.ocs.common.constant;

/**
 * @author tangyixiang
 * @Date 2022/10/12
 */
public class CommonConstants {

    public static final String STATUS_NORMAL = "1";

    public static final String STATUS_DEL = "2";

    // 模糊查询
    public static final String QUERY_LIKE = "like";
    // 精准查询
    public static final String QUERY_EQUAL = "equal";

    // 已对账
    public static final String RECONCILED = "1";
    // 未对账
    public static final String NOT_RECONCILED = "2";
    // 部分对账
    public static final String PART_RECONCILED = "3";

    // 自动对账
    public static final String AUTO_RECONCILIATION = "1";
    // 手动对账
    public static final String MANUAL_RECONCILIATION = "2";
    // 取消对账
    public static final String CANCEL_RECONCILIATION = "3";

    // 借款
    public static final String BORROW = "1";
    // 贷款
    public static final String LOAN = "2";

    // 导入
    public static final String DATA_LOG_IMPORT = "1";
    // 导出
    public static final String DATA_LOG_EXPORT = "2";

    // 应收对账单类型-财政发票类型
    public static final String RECEIVABLE_FINANCE = "1";
    // 应收对账单类型-经营发票类型
    public static final String RECEIVABLE_OPERATE = "2";
    // 应收对账单类型-初始化经营发票类型
    public static final String RECEIVABLE_CUSTOM_FINANCE = "3";
    // 应收对账单类型-初始化财政发票类型
    public static final String RECEIVABLE_CUSTOM_OPERATE = "4";

    // 发票的方向 正向
    public static final String INVOICE_DIRECT_FORWARD = "1";
    // 发票的方向 反向
    public static final String INVOICE_DIRECT_REVERSE = "2";

    /**
     * 分录任务
     */
    public static final Integer TASK_SPLIT = 1;

    /**
     * 字典分组-系统组
     */
    public static final String DICT_SYS_GROUP = "sys";

    /**
     * 字典分组-业务组
     */
    public static final String DICT_BUSI_GROUP = "busi";

    /**
     * 字典分组-业务组
     */
    public static final String DICT_FINANCE_GROUP = "finance";

}
