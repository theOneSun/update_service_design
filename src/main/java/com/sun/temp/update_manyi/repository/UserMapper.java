package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface UserMapper {
    User findByUsername(String username);

    List<User> findByUsernameList(List<String> usernameList);

    List<User> findAll();
}
