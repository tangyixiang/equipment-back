package com.ocs.common.task;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author tangyixiang
 * @Date 2022/11/9
 */
@Data
public class SysJobRuntime {

    private LocalDateTime startTime;

    private String taskId;

    private Integer taskType;
}
