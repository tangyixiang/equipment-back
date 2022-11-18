package com.ocs.busi.domain.dto;

import com.ocs.busi.domain.entity.SysUserExtension;
import com.ocs.common.annotation.Excel;
import com.ocs.common.annotation.Excels;
import com.ocs.common.core.domain.BaseEntity;
import com.ocs.common.core.domain.entity.SysDept;
import com.ocs.common.core.domain.entity.SysRole;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/28
 */
@Data
public class SysUserDto extends BaseEntity {


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 部门对象
     */
    private SysDept dept;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 角色ID
     */
    private Long roleId;

    // 人员性质
    private String hireType;

    private List<SysUserExtension.InspectionQualification> certificate;

    private List<SysUserExtension.CustomizeDimension> dimensions;

}