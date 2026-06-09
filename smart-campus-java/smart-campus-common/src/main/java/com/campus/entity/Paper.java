package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Paper extends BaseEntity {

    private String name;
    private Long courseId;
    private Integer totalScore = 100;
    private Integer passScore = 60;
    private Integer duration = 120;
    private Integer singleCount = 0;
    private Integer singleScore = 0;
    private Integer multiCount = 0;
    private Integer multiScore = 0;
    private Integer judgeCount = 0;
    private Integer judgeScore = 0;
    private Integer questionCount = 0;
    private String status = "草稿";
    private String description;
}
