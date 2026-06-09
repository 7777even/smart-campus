package com.smart.campus.web.biz;

import com.campus.entity.Resource;
import com.smart.campus.web.mappers.WebResourceMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 资源业务（学生端）
 */
@Component
public class ResourceWebBiz {

    private final WebResourceMapper resourceMapper;

    public ResourceWebBiz(WebResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    /**
     * 按课程获取资源列表
     */
    public List<Map<String, Object>> listByCourse(String courseId) {
        List<Resource> resources = resourceMapper.selectByCourseId(courseId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Resource r : resources) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(r.getId()));
            m.put("name", r.getName());
            m.put("type", r.getType());
            m.put("category", r.getCategory());
            m.put("fileSize", r.getFileSize());
            m.put("filePath", r.getFilePath());
            m.put("uploader", r.getUploader());
            m.put("downloads", r.getDownloads());
            m.put("status", r.getStatus());
            m.put("description", r.getDescription());
            result.add(m);
        }
        return result;
    }

    /**
     * 资源详情
     */
    public Map<String, Object> getDetail(String id) {
        Resource resource = resourceMapper.selectById(Long.valueOf(id));
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(resource.getId()));
        m.put("name", resource.getName());
        m.put("type", resource.getType());
        m.put("category", resource.getCategory());
        m.put("fileSize", resource.getFileSize());
        m.put("filePath", resource.getFilePath());
        m.put("uploader", resource.getUploader());
        m.put("downloads", resource.getDownloads());
        m.put("status", resource.getStatus());
        m.put("description", resource.getDescription());
        return m;
    }
}
