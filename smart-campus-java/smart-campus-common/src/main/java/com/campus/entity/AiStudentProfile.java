package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生学业画像实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiStudentProfile extends BaseEntity {

    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long classId;
    private Long majorId;
    private Long departmentId;
    private BigDecimal gpa;
    private BigDecimal attendanceRate;
    private BigDecimal homeworkAvg;
    private BigDecimal examAvg;
    private BigDecimal comprehensiveScore;
    private String riskLevel = "green";
    private String trend = "stable";
    private LocalDateTime lastCalcTime;
}
