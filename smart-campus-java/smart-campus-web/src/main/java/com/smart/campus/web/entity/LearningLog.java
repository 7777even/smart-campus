package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学习日志实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LearningLog extends BaseEntity {

    private Long studentId;
    private Long courseId;
    private Long lessonId;
    private String logType;
    private Long duration;
    private String detail;
}
