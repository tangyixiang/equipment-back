package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * 用户申检派单实体
 */
@TableName(value = "apply_check")
@Data
public class ApplyCheck extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 用户申检单编号
     */
    private String checkCode;

    /**
     * 申检单位名称
     */
    private String checkedClient;

    /**
     * 是否协约客户
     */
    private String agreementClient;

    /**
     * 申检单位地址
     */
    private Date applyOrgAddress;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 申报日期
     */
    private LocalDate applyDate;

    /**
     * 约检日期
     */
    private LocalDate checkDate;

    /**
     * 状态  1 受理
     */
    private String status;

    /**
     * 派单人
     */
    private Long dispatchUserId;

    /**
     * 派单人部门
     */
    private Long dispatchDeptId;

    /**
     * 检验人
     */
    private Long checkUserId;

    /**
     * 缴费状态
     */
    private String payStatus;

}
