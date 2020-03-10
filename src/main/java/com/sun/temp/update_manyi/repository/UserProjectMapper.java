package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.UserProject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface UserProjectMapper {
    int batchInsert(@Param("userProjectList") List<UserProject> userProjectList);

    List<UserProject> getAll();
}
