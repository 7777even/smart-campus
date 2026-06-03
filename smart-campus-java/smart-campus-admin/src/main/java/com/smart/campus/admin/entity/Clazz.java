package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体
 *
 * @see BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Clazz extends BaseEntity {

    private String name;
    private String code;
    private Long departmentId;
    private Long majorId;
    private Integer year;
    private Integer studentCount = 0;
    private Integer status = 1;
    private Integer sort = 0;
    private String description;
}
