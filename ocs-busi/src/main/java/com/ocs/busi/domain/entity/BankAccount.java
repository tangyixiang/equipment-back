package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 银行账号
 *
 * @TableName bank_account
 */
@TableName(value = "bank_account")
@Data
public class BankAccount extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 账户名
     */
    @NotBlank(message = "账户名不能为空")
    private String accountName;

    /**
     * 别名
     */
    @NotBlank(message = "账户别名不能为空")
    private String aliasName;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 开户行名称
     */
    @NotBlank(message = "开户行名称不能为空")
    private String bankName;

    /**
     * 开户日期
     */
    @NotNull(message = "开户日期不能为空")
    private LocalDate openDate;

    /**
     * 删除标志 1 正常 2 删除
     */
    private String del;

}
