package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Announcement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告 Mapper（学生端）
 */
public interface WebAnnouncementMapper extends BaseMapper<Announcement> {

    List<Announcement> selectPublishedList(@Param("keyword") String keyword,
                                           @Param("pageNo") Integer pageNo,
                                           @Param("pageSize") Integer pageSize);

    long selectPublishedCount(@Param("keyword") String keyword);

    Announcement selectById(@Param("id") Long id);
}
