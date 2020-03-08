package com.sun.temp.update_manyi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zhangpeng
 */
@Data
public class User {

    public static final String USERNAME_REGEX = "[a-zA-Z][a-zA-Z0-9@_.]{5,17}";

    private String id;

    protected String createdBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updatedDate;

    @NotNull
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean enabled;

    private String description;

    private String phone;

    private String email;

    private String openId;

    private String unionId;
}
