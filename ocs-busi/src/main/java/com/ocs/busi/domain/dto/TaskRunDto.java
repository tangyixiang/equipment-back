package com.ocs.busi.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author tangyixiang
 * @Date 2022/11/10
 */
@Data
public class TaskRunDto {

    @NotBlank(message = "任务ID不能为空")
    private Long jobId;

    @NotBlank(message = "参数不能为空")
    private String params;
}
