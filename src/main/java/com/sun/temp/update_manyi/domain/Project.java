package com.sun.temp.update_manyi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 满意度项目.
 *
 * @author 张鹏
 */
@Data
public class Project {
    private String id;

    private String name;

    private String icon;

    private Industry industry;

    private Algorithm algorithm = Algorithm.TIER;

    private String scoreTitle;

    private String areaScoreTitle;

    private String areaComparisonTitle;

    private String indexScoreTitle;

    private String improvedMatrixTitle;

    private String topAndBottomScoreTitle;

    private String indexScaleTitle;

    private String individualRankingTitle;

    private Boolean scaleQuestion;

    private Integer scaleLevel;

    @Min(1)
    private Integer topScaleLevel;

    @Min(1)
    private Integer bottomScaleLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUploadDataTime;

    private Double latestScore;

    private Visibility visibility;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updatedDate;

    public enum Visibility {
        /**
         * 公开的
         */
        PUBLIC,

        /**
         * 加密的
         */
        PROTECT,

        /**
         * 私有的
         */
        PRIVATE
    }

    @Deprecated
    public enum Industry {

        /**
         * 房地产.
         */
        REAL_ESTATE,

        /**
         * 消费品.
         */
        CONSUMABLE,

        /**
         * 汽车行业.
         */
        AUTOMOBILE,

        /**
         * 城市创新
         */
        CITY_INNOVATE,
        /**
         * 政府治理
         */
        GOVERNMENT,
        /**
         * 国际关系与社会发展
         */
        INTER_SOCIAL,
        /**
         * 金融
         */
        FINANCIAL,

        /**
         * 公共事务.
         */
        PUBLIC_AFFAIRS
    }

    public enum Algorithm {
        /**
         * 层级计算(只根据下级指标的得分确定当前得分,不考虑末级指标,与末级指标有没有空无关)
         */
        TIER,
        /**
         * 直接求平均数的算法
         */
        AVERAGE,
        /**
         * 高分占比(房地产项目的计算方式)
         */
        TOP_SCORE_PERCENT,
    }
}
