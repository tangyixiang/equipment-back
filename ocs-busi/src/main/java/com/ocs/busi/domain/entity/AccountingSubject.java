package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 会计科目
 *
 * @TableName accounting_subject
 */
@Data
@TableName(value = "accounting_subject")
public class AccountingSubject extends SimpleEntity {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 科目分类
     */
    @NotBlank(message = "科目分类不能为空")
    private String categoryId;

    /**
     * 科目映射关系
     */
    @NotBlank(message = "科目映射关系不能为空")
    private String mappingId;

    // 科目映射的名称
    @TableField(exist = false)
    private String mappingName;

    /**
     * 项目ID
     */
    @NotBlank(message = "项目不能为空")
    private String itemId;

    /**
     * 会计科目值
     */
    @NotBlank(message = "会计科目值不能为空")
    private String value;

    /**
     * 删除标志
     */
    private String del;

}