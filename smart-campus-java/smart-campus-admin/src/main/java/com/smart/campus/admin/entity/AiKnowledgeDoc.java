package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 知识库文档实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeDoc extends BaseEntity {

    private String title;
    private String content;
    private String category;
    private String tags;
    private Integer status = 1;
    private String uploader;
}
