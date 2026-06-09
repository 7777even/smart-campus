package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.VideoProgress;
import org.apache.ibatis.annotations.Param;

/**
 * 视频进度 Mapper（学生端）
 */
public interface VideoProgressMapper extends BaseMapper<VideoProgress> {

    VideoProgress selectByLessonAndStudent(@Param("lessonId") Long lessonId,
                                           @Param("studentId") Long studentId);

    int update(VideoProgress progress);

    int insert(VideoProgress progress);
}
