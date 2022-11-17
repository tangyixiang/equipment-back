package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

/**
 * 数据处理log
 * @TableName data_process_log
 */
@TableName(value ="data_process_log")
@Data
public class DataProcessLog extends SimpleEntity {
    /**
     * id
     */
    private String id;

    /**
     * 过程名称
     */
    private String processName;

    /**
     * 过程状态
     */
    private String processStatus;

    /**
     * 结果路径
     */
    private String resultPath;

    /**
     * 处理类型  1 导入 2 导出
     */
    private String processType;

}
