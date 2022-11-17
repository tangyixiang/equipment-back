package com.ocs.busi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ocs.common.core.domain.SimpleEntity;
import lombok.Data;

/**
 * 会计科目
 */
@Data
@TableName(value = "accounting_subject")
public class AccountingSubject extends SimpleEntity {

    private String id;

    private String parentId;

    private String name;

    private String del;

}
