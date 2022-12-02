package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import java.time.LocalDate;


@Data
@TableName(value = "company_contract_item")
public class CompanyContractItem extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 检测项目ID
     */
    private String checkItemId;

    /**
     * 金额类别 1 单价 2 总价 3 不限定金额
     */
    private String priceType;

    /**
     * 金额
     */
    private String price;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    private String del;

}
