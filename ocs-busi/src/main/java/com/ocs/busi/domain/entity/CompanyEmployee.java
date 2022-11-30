package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 职员信息表
 *
 * @TableName company_employee
 */
@TableName(value = "company_employee")
@Data
public class CompanyEmployee extends SimpleEntity implements Serializable {
    /**
     * 用户ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 员工编号
     */
    private String userCode;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 岗位ID
     */
    private Long postId;

    /**
     * 职务ID
     */
    private Long positionId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 职员状态（0正常 1停用）
     */
    private String status;

    // 1 在编 2 聘用
    private String hireType;

    /**
     * 删除标志（1代表存在 2代表删除）
     */
    private String del;

    @TableField(exist = false)
    private SysUserExtension extensionData;

}