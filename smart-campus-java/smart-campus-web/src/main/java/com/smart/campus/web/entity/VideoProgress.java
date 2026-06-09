package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频进度实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoProgress extends BaseEntity {

    private Long lessonId;
    private Long studentId;
    private Double progressPoint;
    private Long duration;
}
