package com.ocs.busi.domain.dto;

import com.ocs.busi.domain.entity.ClientFinanceInfoItem;
import lombok.Data;

import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/11/3
 */
@Data
public class CompanyClientOrgDto {

    private String id;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人手机号码
     */
    private String contactPhone;

    private List<ClientFinanceInfoItem> itemInfo;
}
