package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学习进度实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LearningProgress extends BaseEntity {

    private Long studentId;
    private Long courseId;
    private Integer completionRate;
    private Integer totalLessons;
    private Integer completedLessons;
}
