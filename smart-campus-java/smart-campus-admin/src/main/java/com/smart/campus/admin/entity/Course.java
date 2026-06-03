package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    private String name;
    private String code;
    private Long departmentId;
    private Long teacherId;
    private String teacherName;
    private String type;
    private Integer credit = 3;
    private Integer hours = 48;
    private String location;
    private Integer status = 1;
    private String description;
    private String cover;
}
