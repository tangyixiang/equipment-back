package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

/**
 * 员工外出表
 * @TableName staff_trip
 */
@TableName(value ="staff_trip")
@Data
public class StaffTrip extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 申检单编号
     */
    private String checkCode;

    /**
     * 部门Id
     */
    private Long deptId;

    /**
     * 岗位Id
     */
    private Long postId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 外出时长(天)
     */
    private String tripDays;

    /**
     * 外出类别
     */
    private String tripType;

    /**
     * 出行方式
     */
    private String tripMethod;

    /**
     * 同行人员
     */
    private String partner;

    /**
     * 地点说明
     */
    private String addressDesc;

    /**
     * 外出事由
     */
    private String outReason;

    /**
     * 图片
     */
    private String pictures;

    /**
     * 实际开始日期
     */
    private LocalDate useStartDate;

    /**
     * 实际结束日期
     */
    private LocalDate useEndDate;

    /**
     * 实际外出时长(天)
     */
    private String useTripDays;

    /**
     * 车辆派遣信息
     */
    private String carDispatchId;

    /**
     * 状态 1 草稿 2 废弃 3 提交 4 完成
     */
    private String status;

}
