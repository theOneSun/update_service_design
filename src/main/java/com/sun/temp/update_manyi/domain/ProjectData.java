package com.sun.temp.update_manyi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 满意度项目数据.
 *
 * @author zhangpeng
 */
@Data
@NoArgsConstructor
public class ProjectData {

    /**
     * 所属项目的 id.
     */
    private UUID projectId;

    /**
     * 评价个体.
     */
    private String individual;

    /**
     * 数据批次.
     */
    private String batch;

    /**
     * 导入批次，标示哪些数据是一起导入的，由程序内部生成,不从外部导入.
     */
    private String importBatch;

    /**
     * 多个区域.
     */
//    @Type(type = "com.vladmihalcea.hibernate.type.array.StringArrayType")
//    @Column(columnDefinition = "varchar(50)[]")
    private String[] areas;

    /**
     * 多个末级指标得分.
     */
//    @Column(columnDefinition = "jsonb")
//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    private Map<String, Double> lastLevelIndexScores = new HashMap<>();

    /**
     * 多个评价人属性.
     */
//    @Column(columnDefinition = "jsonb")
//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    private Map<String, String> peopleAttributes = new HashMap<>();

    /**
     * 多个重要指标得分.
     */
//    @Column(columnDefinition = "jsonb")
//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    private Map<String, Double> importantIndexScores = new HashMap<>();

    /**
     * 数据所属的权限分类
     */
    private String authCategory;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updatedDate;

    public ProjectData(final String batch, final String[] areas, final Map<String, String> peopleAttributes) {
        this.batch = batch;
        this.areas = areas;
        this.peopleAttributes = peopleAttributes;
    }

    public ProjectData(final String individual, final String[] areas) {
        this.individual = individual;
        this.areas = areas;
    }

    public ProjectData(final String individual, final String batch, final String[] areas) {
        this.individual = individual;
        this.batch = batch;
        this.areas = areas;
    }
}
