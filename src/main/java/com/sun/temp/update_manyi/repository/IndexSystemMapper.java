package com.sun.temp.update_manyi.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author sunjian.
 */
@Repository
public interface IndexSystemMapper {
    void updateTimeByImportBatch(@Param("importBatch") String importBatch, @Param("createdTime") LocalDateTime createdTime);
}
