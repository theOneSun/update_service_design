package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface UserMapper {
    User findByUsername(String username);

    List<User> findByUsernameList(List<String> usernameList);

    List<User> findAll();

    List<User> findMoreProjectUser();

    void updateCreatedDate(@Param("userId") String userId, @Param("createdDate") LocalDateTime createdDate);
}
