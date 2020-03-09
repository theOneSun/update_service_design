package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.Project;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface ProjectMapper {
    /**
     * 名称模糊查询
     *
     * @param name
     * @return
     */
    List<Project> findByLikeName(@Param("name") String name);

    /**
     * 项目名称查询
     *
     * @param name 项目名称
     * @return
     */
    Project findByName(@Param("name") String name);

    /**
     * 更新描述
     *
     * @param id          项目id
     * @param description 描述
     */
    void updateDescription(@Param("id") String id, @Param("description") String description);

    /**
     * 更新创建者
     *
     * @param projectId
     * @param userId
     */
    void updateCreatedBy(@Param("projectId") String projectId,@Param("userId") String userId);
}
