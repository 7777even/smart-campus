package com.campus.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 对话记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiConversation extends BaseEntity {

    private Long userId;
    private String userRole;
    private String title;
}
