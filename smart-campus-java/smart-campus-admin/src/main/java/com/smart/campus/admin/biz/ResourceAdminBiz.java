package com.smart.campus.admin.biz;

import com.campus.entity.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源业务（管理端）
 */
@Component
public class ResourceAdminBiz {

    /**
     * 构建资源对象（从上传表单提取）
     */
    public Resource buildResource(MultipartFile file, String name, String type,
                                   String category, String description) {
        Resource resource = new Resource();
        if (name != null && !name.isEmpty()) {
            resource.setName(name);
        } else {
            resource.setName(file.getOriginalFilename());
        }
        resource.setType(type);
        resource.setCategory(category);
        resource.setDescription(description);
        resource.setStatus(1);
        return resource;
    }
}
