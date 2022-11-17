package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户财务信息明细
 *
 * @TableName client_finance_info_item
 */
@Data
@TableName(value = "client_finance_info_item")
public class ClientFinanceInfoItem {

    @TableId
    private String id;

    /**
     * 客户财务信息ID
     */
    private String clientOrgId;

    /**
     * 自定义名称
     */
    private String itemKey;

    /**
     * 自定义的值
     */
    private String itemValue;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
