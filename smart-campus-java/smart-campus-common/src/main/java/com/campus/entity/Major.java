package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业实体
 *
 * @see BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Major extends BaseEntity {

    private String name;
    private String code;
    private Long departmentId;
    private String level;
    private Integer years = 4;
    private Integer status = 1;
    private Integer sort = 0;
    private String description;
}
