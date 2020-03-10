package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.ProjectInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface ProjectInfoMapper {
    List<ProjectInfo> getAll();

    /**
     * 根据账号名称查找项目的最大时间
     *
     * @param account 账号的username
     * @return
     */
    LocalDate findMaxEndTimeByAccount(@Param("account") String account);

    LocalDate findMinBeginTimeByAccount(@Param("account") String account);

    LocalDate getEarlierBeginDate(@Param("codeArray") String[] codeArray);
}
