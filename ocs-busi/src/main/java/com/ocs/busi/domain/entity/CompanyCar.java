package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/12
 */
@Data
@TableName(autoResultMap = true)
public class CompanyCar extends SimpleEntity {

    @TableId
    private String id;
    // 车牌号
    private String plateNumber;
    // 车辆类型
    private String carType;
    // 车座
    private String seater;
    // 品牌
    private String brand;
    // 新旧程度
    private String newPercent;
    // 车身颜色
    private String color;
    // 发动机号
    private String engineNum;
    // 发动机型号
    private String engineModel;
    // 车辆型号
    private String carModel;
    // 大驾号
    private String bigCarNum;
    // 登记证编号
    private String regCertNum;
    // 行驶证号
    private String licenseNum;
    // 1 国产 2 进口
    private String madeInChina;
    // 车位
    private String parking;
    // 最大里程
    private String maxMileage;
    // 最高时速
    private String maxSpeed;
    // 燃料类型
    private String energyType;
    // 油耗
    private String fuelConsumption;
    // 初始公里数
    private String initialKilometer;
    // 服务路线
    private String lineName;
    // 投保日期
    private LocalDate buyInsuranceDate;
    // 年检日期
    private LocalDate yearCheckDate;
    // 底盘号
    private String chassisNum;
    // 制造厂名字
    private String factoryName;
    // 车辆负重
    private String carLoad;
    // 轮胎规格
    private String tireSpecifications;
    // 轴距
    private String wheelbase;
    // 购买日期
    private LocalDate buyDate;
    // 购买价格（万元）
    private String price;
    // 购买处
    private String saleStore;
    // 责任人姓名
    private String responsibleName;
    // 责任人电话
    private String responsiblePhone;
    // 附件
    private String attachment;
    // 备注
    private String remark;
    // 车辆图片
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> pictures;
    // 状态 1 正常 2 停用 3 报废
    private String status;

    private String del;

}
