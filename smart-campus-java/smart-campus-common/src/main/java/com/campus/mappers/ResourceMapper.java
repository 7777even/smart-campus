package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源 Mapper
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    List<Resource> selectList(@Param("keyword") String keyword,
                              @Param("type") String type,
                              @Param("category") String category);

    long selectCount(@Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("category") String category);
}
