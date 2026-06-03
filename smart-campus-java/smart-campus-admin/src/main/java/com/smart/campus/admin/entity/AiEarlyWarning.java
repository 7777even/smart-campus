package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学业预警记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiEarlyWarning extends BaseEntity {

    private Long studentId;
    private Long profileId;
    private String warningType;     // attendance/homework/exam/comprehensive
    private String level;           // red/yellow
    private BigDecimal score;
    private BigDecimal threshold;
    private String description;
    private String suggestion;
    private String status = "pending";  // pending/resolved/ignored
    private String resolver;
    private LocalDateTime resolveTime;
}
