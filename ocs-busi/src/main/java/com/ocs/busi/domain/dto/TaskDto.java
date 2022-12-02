package com.ocs.busi.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tangyixiang
 * @Date 2022/11/10
 */
@Data
public class TaskDto {

    private String taskName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String taskStatus;
}
