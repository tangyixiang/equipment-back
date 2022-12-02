package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 公司客户
 */
@Data
@TableName(value = "company_client_org")
public class CompanyClientOrg extends SimpleEntity {
    /**
     * id
     */
    @TableId
    @NotBlank(message = "客户ID不能为空")
    private String id;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    private String name;

    /**
     * 统一社会信用码
     */
    @NotBlank(message = "统一社会信用码不能为空")
    private String socialCreditCode;

    /**
     * 客户地址
     */
    private String address;

    /**
     * 所在地区
     */
    private String region;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人手机号码
     */
    private String contactPhone;

    private String del;
}
