package com.campus.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 习题实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Exercise extends BaseEntity {

    private Long courseId;
    private String type;
    private String difficulty;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String analysis;
}
