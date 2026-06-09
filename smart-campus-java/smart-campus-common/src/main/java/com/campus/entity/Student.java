package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生实体
 *
 * @see BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends BaseEntity {

    private String studentNo;
    private String name;
    private String gender;
    private Long departmentId;
    private Long majorId;
    private Long classId;
    private String phone;
    private String email;
    private String password;
    private String status = "在读";
    private String address;
    private String avatar;
}
