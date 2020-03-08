package com.sun.temp.update_manyi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sunjian.
 */
@Data
@AllArgsConstructor
public class UserLogin {
    private String id;
    private String userId;
    private LocalDateTime lastLoginTime;
}
