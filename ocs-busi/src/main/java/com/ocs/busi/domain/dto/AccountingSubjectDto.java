package com.ocs.busi.domain.dto;

import com.ocs.busi.domain.entity.AccountingSubject;
import lombok.Data;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/8
 */
@Data
public class AccountingSubjectDto {

    private AccountingSubject subject;

    private List<AccountingSubjectDto> child;
}
