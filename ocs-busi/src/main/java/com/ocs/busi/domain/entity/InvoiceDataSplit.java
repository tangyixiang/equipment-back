package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ocs.common.annotation.Excel;
import lombok.Data;

/**
 * @author tangyixiang
 * @Date 2022/11/11
 */
@Data
public class InvoiceDataSplit {

    /**
     * 分录类别
     */
    @Excel(name = "分录类别")
    @TableField(exist = false)
    protected String splitType;

    @Excel(name = "分录名称")
    private String name;

    /**
     * 日期
     */
    @Excel(name = "日期")
    protected String date;

    /**
     * 凭证类型
     */
    @Excel(name = "凭证类型")
    protected String certificateType;

    /**
     * 凭证号
     */
    @Excel(name = "凭证号")
    protected Integer certificateId;

    /**
     * 科目编号
     */
    @Excel(name = "科目编号")
    protected String subjectCode;

    /**
     * 摘要
     */
    @Excel(name = "摘要")
    protected String summary;

    /**
     * 借方
     */
    @Excel(name = "借方", cellType = Excel.ColumnType.NUMERIC)
    protected String borrow;

    /**
     * 贷方
     */
    @Excel(name = "贷方", cellType = Excel.ColumnType.NUMERIC)
    protected String loan;

    /**
     * 附件张数
     */
    @Excel(name = "附件张数")
    protected String attachmentNum ="0";

    /**
     * 辅助用途
     */
    @Excel(name = "辅助用途")
    protected String useFor;

    /**
     * 预算项目编码
     */
    @Excel(name = "预算项目编码")
    protected String budgetProjectCode;

    /**
     * 功能分类编码
     */
    @Excel(name = "功能分类编码")
    protected String funcClassificCode;

    /**
     * 部门经济分类编码
     */
    @Excel(name = "部门经济分类编码")
    protected String deptEconomyCode;

    /**
     * 往来编码
     */
    @Excel(name = "往来编码")
    protected String contactsCode;

    /**
     * 差异项编码
     */
    @Excel(name = "差异项编码")
    protected String divergenceCode;

    /**
     * 资金性质编码
     */
    @Excel(name = "资金性质编码")
    protected String fundsCode;
}
