package com.campus.entity;

import com.campus.entity.BaseEntity;

/**
 * 公告实体
 */
public class Announcement extends BaseEntity {

    private String title;
    private String content;
    private String publisher;
    private String level;      // 紧急/重要/普通
    private String status = "草稿";     // 已发布/草稿

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
