package com.smart.campus.web.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试记录实体（学生端）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamRecord extends BaseEntity {

    private Long examId;
    private Long studentId;
    private Integer score;
    private String status;
}
