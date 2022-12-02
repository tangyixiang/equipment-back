package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @TableName apply_check_item
 */
@TableName(value = "apply_check_item")
@Data
public class ApplyCheckItem {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 检验单ID
     */
    private String applyCheckId;

    /**
     * 用户申检项目
     */
    private String checkItemName;

    /**
     * 设备注册码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 申检数量
     */
    private Integer checkNum;

    /**
     * 客户类型 1 经营  2 法定
     */
    private String clientType;

    /**
     * 检验项目明细ID
     */
    private String checkItemId;

    /**
     * 金额
     */
    private String price;

    /**
     * 删除
     */
    private String del;

}
