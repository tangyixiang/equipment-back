package com.ocs.busi.domain.entity;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ocs.busi.config.GenericJacksonJsonTypeHandler;
import com.ocs.busi.domain.model.ReceivableBankFlowMapping;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@TableName(autoResultMap = true)
public class CompanyReceivables extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 期间
     */
    @NotBlank(message = "期间不能为空")
    private String period;

    /**
     * 来源 1 财政发票  2 经营发票 3 初始化数据
     */
    private String sourceType;

    /**
     * 开票日期
     */
    @NotNull(message = "开票日期不能为空")
    private LocalDate invoicingDate;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    private String clientOrgName;

    /**
     * 审检日期
     */
    private LocalDate applyCheckDate;

    /**
     * 应收金额
     */
    @NotNull(message = "应收金额不能为空")
    private Double receivableAmount;

    /**
     * 对账金额
     */
    private Double confirmAmount = 0d;

    /**
     * 对账余额
     */
    private Double unConfirmAmount;

    /**
     * 对账标识
     */
    private String reconciliationFlag;

    /**
     * 对账类别
     */
    private String reconciliationModel;

    /**
     * 对账银行账号
     */
    private String bankAccount;

    /**
     * 发票唯一标识
     */
    private String invoiceId;

    /**
     * 方向
     */
    private String direction;

    /**
     * 有效
     */
    private Boolean valid;

    /**
     * 应收对账ID
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> associationId = new ArrayList<>();

    /**
     * 备注
     */
    @TableField(typeHandler = GenericJacksonJsonTypeHandler.class)
    private List<ReceivableBankFlowMapping> remark = new ArrayList<>();


    public void setReceivableAmount(Double receivableAmount) {
        this.receivableAmount = NumberUtil.round(receivableAmount, 2).doubleValue();
    }

    public void setConfirmAmount(Double confirmAmount) {
        this.confirmAmount = NumberUtil.round(confirmAmount, 2).doubleValue();
    }

    public void setUnConfirmAmount(Double unConfirmAmount) {
        this.unConfirmAmount = NumberUtil.round(unConfirmAmount, 2).doubleValue();
    }
}