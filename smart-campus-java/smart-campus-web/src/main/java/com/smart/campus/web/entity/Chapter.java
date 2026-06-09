package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 章节实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Chapter extends BaseEntity {

    private String courseId;
    private String name;
    private Integer sort;
    private Integer status;
}
