package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课时资源实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LessonResource extends BaseEntity {

    private Long lessonId;
    private Long resourceId;
    private String resourceType;
    private String resourceUrl;
}
