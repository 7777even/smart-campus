package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Resource extends BaseEntity {

    private String name;
    private String type;
    private String category;
    private Long fileSize = 0L;
    private String filePath;
    private String uploader;
    private Integer downloads = 0;
    private Integer status = 1;
    private String description;
}
