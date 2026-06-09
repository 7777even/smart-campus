package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生选课实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentCourse extends BaseEntity {

    private Long studentId;
    private Long courseId;
    private Integer score;
}
