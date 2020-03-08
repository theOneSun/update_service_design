package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.DataUpload;
import com.sun.temp.update_manyi.domain.Project;
import com.sun.temp.update_manyi.domain.ProjectTime;
import com.sun.temp.update_manyi.domain.User;
import com.sun.temp.update_manyi.domain.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author sunjian.
 */
@Repository
public interface ComplexMapper {
    /**
     * 查寻所有项目开始结束时间
     *
     * @return 所有项目开始结束时间
     */
    List<ProjectTime> getAllProjectTime();

    /**
     * 查询project
     *
     * @param id 主键
     * @return project
     */
    Project findProjectById(@Param("id") UUID id);

    /**
     * 查上传记录
     *
     * @param projectId 项目id
     * @return 上传记录
     */
    List<DataUpload> findDataUploadByPId(@Param("projectId") UUID projectId);

    /**
     * 查询项目的用户
     *
     * @param projectId 项目id
     * @return 项目用户
     */
    List<User> findUserByProjectId(@Param("projectId") UUID projectId);

    /**
     * 查询创建者
     *
     * @param projectId 项目id
     * @return 项目的创建者
     */
    User findCreator(@Param("projectId") UUID projectId);

    /**
     * 批量更新用户时间
     *
     * @param updateUserList 需要更新的用户集合
     * @return 修改的数量
     */
    int updateUsers(@Param("userList") List<User> updateUserList);

    /**
     * 批量更新项目时间
     *
     * @param updateProjectList 更新的项目集合
     * @return 更新的数量
     */
    int updateProjects(@Param("projectList") List<Project> updateProjectList);

    /**
     * 批量更新数据上传记录的时间
     *
     * @param updateDataUploadList 更新的数据上传记录集合
     * @return 更新的数量
     */
    int updateDataUploads(@Param("uploadList") List<DataUpload> updateDataUploadList);

    /**
     * 批量新增用户登录记录
     *
     * @param insertUserLoginList 用户登录记录
     * @return 插入记录数
     */
    int batchInsertUserLogin(@Param("insertList") List<UserLogin> insertUserLoginList);
}
