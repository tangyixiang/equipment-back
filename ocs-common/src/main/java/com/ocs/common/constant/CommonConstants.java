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
    public static final String RECONCILED = "Y";
    // 未对账
    public static final String NOT_RECONCILED = "Y";

    // 自动对账
    public static final String AUTO_RECONCILIATION = "1";
    // 手动对账
    public static final String MANUAL_RECONCILIATION = "2";

    // 借款
    public static final String BORROW = "1";
    // 贷款
    public static final String LOAN = "2";

    // 导入
    public static final String DATA_LOG_IMPORT = "1";
    // 导出
    public static final String DATA_LOG_EXPORT = "2";

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

}
