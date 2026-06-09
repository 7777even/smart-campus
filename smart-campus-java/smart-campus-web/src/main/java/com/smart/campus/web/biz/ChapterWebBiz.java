package com.smart.campus.web.biz;

import com.campus.entity.Resource;
import com.smart.campus.web.entity.Chapter;
import com.smart.campus.web.entity.Lesson;
import com.smart.campus.web.entity.LessonResource;
import com.smart.campus.web.mappers.ChapterMapper;
import com.smart.campus.web.mappers.LessonMapper;
import com.smart.campus.web.mappers.LessonResourceMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 章节/课时业务（学生端）
 */
@Component
public class ChapterWebBiz {

    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final LessonResourceMapper lessonResourceMapper;

    public ChapterWebBiz(ChapterMapper chapterMapper,
                         LessonMapper lessonMapper,
                         LessonResourceMapper lessonResourceMapper) {
        this.chapterMapper = chapterMapper;
        this.lessonMapper = lessonMapper;
        this.lessonResourceMapper = lessonResourceMapper;
    }

    /**
     * 获取课程章节结构，每章嵌套课时列表
     */
    public List<Map<String, Object>> getByCourseId(String courseId) {
        List<Chapter> chapters = chapterMapper.selectByCourseId(courseId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Chapter chapter : chapters) {
            Map<String, Object> chapterMap = new HashMap<>();
            chapterMap.put("id", String.valueOf(chapter.getId()));
            chapterMap.put("courseId", chapter.getCourseId());
            chapterMap.put("name", chapter.getName());
            chapterMap.put("sort", chapter.getSort());
            chapterMap.put("status", chapter.getStatus());
            chapterMap.put("createTime", chapter.getCreateTime());
            chapterMap.put("updateTime", chapter.getUpdateTime());

            List<Lesson> lessons = lessonMapper.selectByChapterId(chapter.getId());
            chapterMap.put("lessons", lessons);

            result.add(chapterMap);
        }
        return result;
    }

    /**
     * 获取章节下的课时列表
     */
    public List<Map<String, Object>> getLessonsByChapterId(String chapterId) {
        Long chapterIdLong = Long.valueOf(chapterId);
        List<Lesson> lessons = lessonMapper.selectByChapterId(chapterIdLong);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Lesson l : lessons) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(l.getId()));
            m.put("chapterId", l.getChapterId());
            m.put("courseId", l.getCourseId());
            m.put("name", l.getName());
            m.put("resourceType", l.getResourceType());
            m.put("resourceUrl", l.getResourceUrl());
            m.put("duration", l.getDuration());
            m.put("description", l.getDescription());
            m.put("sort", l.getSort());
            m.put("status", l.getStatus());
            result.add(m);
        }
        return result;
    }

    /**
     * 课时详情
     */
    public Map<String, Object> getLessonDetail(String lessonId) {
        Lesson lesson = lessonMapper.selectById(Long.valueOf(lessonId));
        if (lesson == null) {
            throw new RuntimeException("课时不存在");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(lesson.getId()));
        m.put("chapterId", lesson.getChapterId());
        m.put("courseId", lesson.getCourseId());
        m.put("name", lesson.getName());
        m.put("resourceType", lesson.getResourceType());
        m.put("resourceUrl", lesson.getResourceUrl());
        m.put("duration", lesson.getDuration());
        m.put("description", lesson.getDescription());
        m.put("sort", lesson.getSort());
        m.put("status", lesson.getStatus());
        return m;
    }

    /**
     * 课时关联的资源列表
     */
    public List<Map<String, Object>> getResourcesByLessonId(String lessonId) {
        List<LessonResource> resources = lessonResourceMapper.selectByLessonId(Long.valueOf(lessonId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (LessonResource r : resources) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(r.getId()));
            m.put("lessonId", r.getLessonId());
            m.put("resourceType", r.getResourceType());
            m.put("resourceUrl", r.getResourceUrl());
            result.add(m);
        }
        return result;
    }
}
