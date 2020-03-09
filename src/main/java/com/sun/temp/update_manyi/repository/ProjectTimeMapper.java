package com.sun.temp.update_manyi.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * @author sunjian.
 */
@Repository
public interface ProjectTimeMapper {
    void updateBeginEndTime(@Param("projectId") String projectId, @Param("beginTime") LocalDate beginTime, @Param("endTime") LocalDate endTime);
}
