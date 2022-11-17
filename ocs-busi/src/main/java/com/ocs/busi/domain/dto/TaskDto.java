package com.ocs.busi.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
