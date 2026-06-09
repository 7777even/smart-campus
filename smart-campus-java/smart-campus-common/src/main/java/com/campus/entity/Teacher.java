package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师实体
 *
 * @see BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseEntity {

    private String teacherNo;
    private String name;
    private String gender;
    private Long departmentId;
    private String title;
    private String degree;
    private String phone;
    private String email;
    private String password;
    private Integer status = 1;
    private String intro;
    private String avatar;
}
