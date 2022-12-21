package com.ocs.busi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivableBankFlowMapping {

    private String bankFlowId;

    private Double usePrice;
}
