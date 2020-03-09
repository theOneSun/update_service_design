package com.sun.temp.update_manyi.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 项目信息
 *
 * @author sunjian.
 */
@Data
public class ProjectInfo {
    private UUID id;
    private String code;
    private String name;
    private String username;
    private String customerName;
    private LocalDate beginTime;
    private LocalDate endTime;
    //是否培训项目
    private boolean train;
    //培训项目所属行业
    private String trainIndustry;
}
