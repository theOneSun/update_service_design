package com.sun.temp.update_manyi.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author sunjian.
 */
@Repository
public interface ProjectDataMapper {
    void updateTimeByImportBatch(@Param("importBatch") String importBatch, @Param("createdTime") LocalDateTime createdTime);
}
