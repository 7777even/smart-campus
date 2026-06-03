package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Exam extends BaseEntity {

    private String name;
    private Long courseId;
    private Long paperId;
    private String examDate;
    private String startTime;
    private Integer duration = 120;
    private String location;
    private String invigilator;
    private Integer totalStudents = 0;
    private Integer attendedStudents = 0;
    private String status = "待开始";
    private String remark;
}
