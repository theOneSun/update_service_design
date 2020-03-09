package com.sun.temp.update_manyi.service;

import com.sun.temp.update_manyi.domain.Project;
import com.sun.temp.update_manyi.domain.ProjectInfo;
import com.sun.temp.update_manyi.domain.User;
import com.sun.temp.update_manyi.domain.UserProject;
import com.sun.temp.update_manyi.repository.ProjectInfoMapper;
import com.sun.temp.update_manyi.repository.ProjectMapper;
import com.sun.temp.update_manyi.repository.ProjectTimeMapper;
import com.sun.temp.update_manyi.repository.UserMapper;
import com.sun.temp.update_manyi.repository.UserProjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sunjian.
 */
@Service
@Slf4j
public class DesignService {
    private final ProjectInfoMapper projectInfoMapper;

    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;
    private final UserProjectMapper userProjectMapper;
    private final ProjectTimeMapper projectTimeMapper;

    @Autowired
    public DesignService(ProjectInfoMapper projectInfoMapper, ProjectMapper projectMapper, UserMapper userMapper,
                         UserProjectMapper userProjectMapper, ProjectTimeMapper projectTimeMapper) {
        this.projectInfoMapper = projectInfoMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.userProjectMapper = userProjectMapper;
        this.projectTimeMapper = projectTimeMapper;
    }

    @Transactional
    public void modifyCreater() {
        /*
        1 查询project_info
        2.1 根据账号修改对应的project创建者id
        2.2 根据客户账号增加userProject
        3.补充project_time
         */
        //要增加的客户账号权限
        List<UserProject> userProjectList = new ArrayList<>();

        //项目信息
        final List<ProjectInfo> projectInfos = projectInfoMapper.getAll();

        //查询用户
        /*final List<String> usernameList = projectInfos.stream()
                                                 .map(ProjectInfo::getUsername)
                                                 .collect(Collectors.toList());*/
        final List<User> userList = userMapper.findAll();
        final Map<String, String> userMap = userList.stream()
                                                    .collect(Collectors.toMap(User::getUsername, User::getId));


        for (ProjectInfo projectInfo : projectInfos) {
            final String code = projectInfo.getCode();
            final Project project;
            if (projectInfo.isTrain()) {
                //培训类项目
                //根据培训行业查询项目
                project = projectMapper.findByName(projectInfo.getTrainIndustry());
                final String description = joinDescription(project, projectInfo.getCode());
                projectMapper.updateDescription(project.getId(), description);
                userProjectList.add(new UserProject(project.getId(), userMap.get(projectInfo.getUsername()),
                                                    UserProject.SourceType.SHARE));
                userProjectList.add(new UserProject(project.getId(), userMap.get(projectInfo.getCustomerName()),
                                                    UserProject.SourceType.SHARE));
            } else {
                //非培训类项目
                //根据c号查找
                List<Project> iLikeList = projectMapper.findByLikeName(code);
                if (Objects.isNull(iLikeList)) {
                    log.error("未查询到项目:" + code);
                } else {
                    log.info("可对应项目:" + code);
                    final String createdId = userMap.get(projectInfo.getUsername());
                    final String customerId = userMap.get(projectInfo.getCustomerName());
                    //修改创建者
                    for (Project p : iLikeList) {
                        projectMapper.updateCreatedBy(p.getId(),createdId);

                        userProjectList.add(new UserProject(p.getId(), createdId, UserProject.SourceType.SELF));
                        userProjectList.add(new UserProject(p.getId(), customerId, UserProject.SourceType.SHARE));
                        projectTimeMapper.updateBeginEndTime(p.getId(),projectInfo.getBeginTime(),projectInfo.getEndTime());
                    }

                }

            }
        }
        //去重
        final List<UserProject> insertList = userProjectList.stream()
                                                         .distinct()
                                                         .collect(Collectors.toList());
        log.info("去掉重复的user_project" + (userProjectList.size() - insertList.size()) + "条");

        final int userProjectInsert = userProjectMapper.batchInsert(insertList);
        log.info("userProject插入了" + userProjectInsert + "条");
    }


    /**
     * 拼接备注中的项目号
     *
     * @param code 项目c号
     */
    private String joinDescription(Project project, String code) {
        final String description = project.getDescription();
        if (StringUtils.isBlank(description)) {
            return code;
        } else {
            return String.join(",", description, code);
        }
    }
}
