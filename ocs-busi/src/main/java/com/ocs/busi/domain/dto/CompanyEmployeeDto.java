package com.ocs.busi.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ocs.busi.domain.entity.SysUserExtension;
import com.ocs.common.core.domain.entity.SysDept;
import lombok.Data;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/28
 */
@Data
public class CompanyEmployeeDto {


    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 部门ID
     */
    private Long deptId;


    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机号码
     */
    private String phonenumber;


    /**
     * 密码
     */
    private String password;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private String sex;

    /**
     * 职员状态（0正常 1停用）
     */
    private String status;

    /**
     * 部门对象
     */
    private SysDept dept;

    /**
     * 岗位组
     */
    private Long postId;

    /**
     * 角色ID
     */
    private String positionName;

    // 人员性质
    private String hireType;

    private List<SysUserExtension.InspectionQualification> certificate;

    private List<SysUserExtension.CustomizeDimension> dimensions;

}
