package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

/**
 * @TableName apply_check_item_logs
 */
@TableName(value = "apply_check_item_logs")
@Data
public class ApplyCheckItemLogs extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 检测单ID
     */
    private String applyCheckId;

    /**
     * 描述
     */
    private String desc;

}
