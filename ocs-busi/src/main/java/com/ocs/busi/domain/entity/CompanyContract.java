package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * @TableName company_contract
 */
@Data
@TableName(value = "company_contract", autoResultMap = true)
public class CompanyContract extends SimpleEntity {

    @TableId
    private String id;

    /**
     * 合同名称
     */
    @NotBlank(message = "合同名称不能为空")
    private String name;

    /**
     * 合同编码
     */
    private String contractCode;

    /**
     * 金额
     */
    private String price;

    /**
     * 合同类型 1 固定期  2 无固定期
     */
    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    /**
     * 合同签订日期
     */
    private LocalDate contractSignDate;

    /**
     * 开始日期
     */
    @NotBlank(message = "合同开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotBlank(message = "合同结束日期不能为空")
    private LocalDate endDate;

    /**
     * 合同有效期
     */
    private String contractPeriod;

    /**
     * 合同签订部门ID
     */
    @NotBlank(message = "合同签订部门不能为空")
    private Long deptId;

    /**
     * 乙方客户ID
     */
    private String sellOrgId;

    /**
     * 附件
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态  1 生效  2 失效
     */
    private String status;

    private String del;

}
