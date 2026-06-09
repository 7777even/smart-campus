package com.smart.campus.web.biz;

import com.campus.entity.Course;
import com.smart.campus.web.entity.LearningLog;
import com.smart.campus.web.entity.LearningProgress;
import com.smart.campus.web.entity.VideoProgress;
import com.smart.campus.web.mappers.LearningLogMapper;
import com.smart.campus.web.mappers.LearningProgressMapper;
import com.smart.campus.web.mappers.VideoProgressMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 学习进度业务（学生端）
 */
@Component
public class LearningWebBiz {

    private final LearningProgressMapper learningProgressMapper;
    private final VideoProgressMapper videoProgressMapper;
    private final LearningLogMapper learningLogMapper;

    public LearningWebBiz(LearningProgressMapper learningProgressMapper,
                          VideoProgressMapper videoProgressMapper,
                          LearningLogMapper learningLogMapper) {
        this.learningProgressMapper = learningProgressMapper;
        this.videoProgressMapper = videoProgressMapper;
        this.learningLogMapper = learningLogMapper;
    }

    /**
     * 获取课程学习进度
     */
    public Map<String, Object> getProgress(Long studentId, String courseId) {
        LearningProgress progress = learningProgressMapper.selectByStudentAndCourse(studentId, Long.valueOf(courseId));
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("courseId", courseId);

        if (progress != null) {
            result.put("completionRate", progress.getCompletionRate());
            result.put("totalLessons", progress.getTotalLessons());
            result.put("completedLessons", progress.getCompletedLessons());
        } else {
            result.put("completionRate", 0);
            result.put("totalLessons", 0);
            result.put("completedLessons", 0);
        }
        return result;
    }

    /**
     * 记录学习进度
     */
    public void recordProgress(Long studentId, String courseId, String lessonId, Integer progress) {
        Long courseIdLong = Long.valueOf(courseId);
        Long lessonIdLong = lessonId != null ? Long.valueOf(lessonId) : null;

        // 更新或创建学习进度
        LearningProgress lp = learningProgressMapper.selectByStudentAndCourse(studentId, courseIdLong);
        if (lp == null) {
            lp = new LearningProgress();
            lp.setStudentId(studentId);
            lp.setCourseId(courseIdLong);
            lp.setCompletionRate(0);
            lp.setTotalLessons(0);
            lp.setCompletedLessons(0);
            learningProgressMapper.insert(lp);
        }

        lp.setCompletedLessons(lp.getCompletedLessons() + 1);
        if (lp.getTotalLessons() > 0) {
            lp.setCompletionRate(
                    (int) Math.round((double) lp.getCompletedLessons() / lp.getTotalLessons() * 100));
        }
        learningProgressMapper.update(lp);

        // 记录学习日志
        LearningLog log = new LearningLog();
        log.setStudentId(studentId);
        log.setCourseId(courseIdLong);
        log.setLessonId(lessonIdLong);
        log.setLogType("lesson_complete");
        log.setDuration(progress != null ? progress.longValue() : 0L);
        log.setDetail("完成课时学习");
        learningLogMapper.insert(log);
    }

    /**
     * 获取视频播放进度
     */
    public Map<String, Object> getVideoProgress(Long studentId, String lessonId) {
        VideoProgress vp = videoProgressMapper.selectByLessonAndStudent(
                Long.valueOf(lessonId), studentId);
        Map<String, Object> result = new HashMap<>();
        result.put("lessonId", lessonId);
        if (vp != null) {
            result.put("progressPoint", vp.getProgressPoint());
            result.put("duration", vp.getDuration());
        } else {
            result.put("progressPoint", 0.0);
            result.put("duration", 0L);
        }
        return result;
    }

    /**
     * 记录视频播放进度
     */
    public void recordVideoProgress(Long studentId, String lessonId, Double playTime) {
        VideoProgress vp = videoProgressMapper.selectByLessonAndStudent(
                Long.valueOf(lessonId), studentId);
        if (vp == null) {
            vp = new VideoProgress();
            vp.setLessonId(Long.valueOf(lessonId));
            vp.setStudentId(studentId);
        }
        vp.setProgressPoint(playTime);
        vp.setDuration(playTime != null ? playTime.longValue() : 0L);

        if (vp.getId() == null) {
            videoProgressMapper.insert(vp);
        } else {
            videoProgressMapper.update(vp);
        }
    }

    /**
     * 获取学习日志（分页）
     */
    public Map<String, Object> getLogs(Long studentId, int pageNo, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        if (studentId == null) {
            result.put("list", Collections.emptyList());
            result.put("total", 0);
            return result;
        }
        long total = learningLogMapper.countByStudentId(studentId);
        List<LearningLog> logs = learningLogMapper.selectByStudentId(studentId, pageNo, pageSize);
        List<Map<String, Object>> list = new ArrayList<>();
        for (LearningLog log : logs) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", log.getId());
            m.put("studentId", log.getStudentId());
            m.put("courseId", log.getCourseId());
            m.put("lessonId", log.getLessonId());
            m.put("logType", log.getLogType());
            m.put("duration", log.getDuration());
            m.put("detail", log.getDetail());
            m.put("createTime", log.getCreateTime());
            list.add(m);
        }
        result.put("list", list);
        result.put("total", total);
        return result;
    }
}
