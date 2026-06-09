package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 院系实体
 *
 * @see BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {

    private String name;
    private String code;
    private String leader;
    private String phone;
    private Integer status = 1;
    private Integer sort = 0;
    private String description;
}
