package com.sun.temp.update_manyi.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author sunjian.
 */
@Repository
public interface UserLoginMapper {
    void updateLastLoginTime(@Param("lastLoginTime") LocalDateTime lastLoginTime, @Param("userId") String userId);
}
