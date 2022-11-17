package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;


@Data
@TableName(value ="bank_flow_split")
public class BankFlowSplit extends SimpleEntity {
    /**
     * id
     */
    private String id;

    /**
     * 银行流水ID
     */
    private String bankFlowId;

    /**
     * 拆分价格
     */
    private Double price;

    /**
     * 关联的对账ID
     */
    private String associationId;

    private String del;

}
