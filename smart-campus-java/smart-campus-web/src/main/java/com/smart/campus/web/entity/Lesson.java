package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课时实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Lesson extends BaseEntity {

    private Long chapterId;
    private String name;
    private String courseId;
    private String resourceType;
    private String resourceUrl;
    private Integer duration;
    private String description;
    private Integer sort;
    private Integer status;
}
