package com.sun.temp.update_manyi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author zhangpeng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProject {
    private String projectId;

    private String userId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    public enum SourceType {
        /**
         * 自建的
         */
        SELF,
        /**
         * 分享的
         */
        SHARE
    }
}
