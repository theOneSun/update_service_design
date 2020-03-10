package com.sun.temp.update_manyi.service;

import com.sun.temp.update_manyi.domain.Project;
import com.sun.temp.update_manyi.domain.ProjectInfo;
import com.sun.temp.update_manyi.domain.User;
import com.sun.temp.update_manyi.domain.UserProject;
import com.sun.temp.update_manyi.repository.ProjectInfoMapper;
import com.sun.temp.update_manyi.repository.ProjectMapper;
import com.sun.temp.update_manyi.repository.ProjectTimeMapper;
import com.sun.temp.update_manyi.repository.UserLoginMapper;
import com.sun.temp.update_manyi.repository.UserMapper;
import com.sun.temp.update_manyi.repository.UserProjectMapper;
import com.sun.temp.update_manyi.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
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
    private final UserLoginMapper userLoginMapper;

    @Autowired
    public DesignService(ProjectInfoMapper projectInfoMapper, ProjectMapper projectMapper, UserMapper userMapper,
                         UserProjectMapper userProjectMapper, ProjectTimeMapper projectTimeMapper,
                         UserLoginMapper userLoginMapper) {
        this.projectInfoMapper = projectInfoMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.userProjectMapper = userProjectMapper;
        this.projectTimeMapper = projectTimeMapper;
        this.userLoginMapper = userLoginMapper;
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
            //-------------
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
                        projectMapper.updateCreatedBy(p.getId(), createdId);

                        userProjectList.add(new UserProject(p.getId(), createdId, UserProject.SourceType.SELF));
                        userProjectList.add(new UserProject(p.getId(), customerId, UserProject.SourceType.SHARE));
                        projectTimeMapper.updateBeginEndTime(p.getId(), projectInfo.getBeginTime(),
                                                             projectInfo.getEndTime());
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
     * 找出excel里有,但是系统里没有的项目号
     */
    public void findNotInSystemProject() {
        //查询所有project_info
        List<String> noProCodeList = new ArrayList<>();
        final List<ProjectInfo> projectInfoList = projectInfoMapper.getAll();
        for (ProjectInfo projectInfo : projectInfoList) {
            final List<Project> byCodeList = projectMapper.findByCode(projectInfo.getCode());
            if (ObjectUtils.isEmpty(byCodeList)) {
                noProCodeList.add(projectInfo.getCode());
            }

        }
        log.info("打印系统没有项目的项目号: ");
        noProCodeList.forEach(System.out::println);
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

    /**
     * 更新项目人员最后登录时间
     */
    public void updateUserLogin() {
        /*
        1.找出有多个项目的账号user_id
        2.根据账号user_id找出对应的项目project_info,找出最晚的时间
        3.user_login的时间就是最晚时间加一周以内的时间
         */
        //超过一个项目的项目账号
        List<User> userList = userMapper.findMoreProjectUser();

        for (User user : userList) {
            //在project_info中找最大时间
            LocalDate lastEndTime = projectInfoMapper.findMaxEndTimeByAccount(user.getUsername());
            //找最小时间
            LocalDate earlierBeginTime = projectInfoMapper.findMinBeginTimeByAccount(user.getUsername());

            //生成创建时间
            final LocalDateTime createdDate = TimeUtils.workTime(earlierBeginTime);
            //更新用户创建时间
            userMapper.updateCreatedDate(user.getId(),createdDate);

            //生成一个登录时间
            final LocalDateTime lastLoginTime = TimeUtils.workTime(lastEndTime);
            //更新用户登录记录
            userLoginMapper.updateLastLoginTime(lastLoginTime,user.getId());
        }
    }

    /**
     * 修改培训类项目创建时间
     */
    public void updateTrainProjectCreatedDate(){
        /*
        1.查询培训类项目(暂定为description不为空的)
        2.根据项目号查询最早的开始时间
        3.根据最早时间+1天(保证项目创建在账号创建之后)生成具体时间
        4.更新project
         */
        List<Project> projectList = projectMapper.findTrainProject();

        for (Project project : projectList) {
            final String description = project.getDescription();
            final String[] codeArray = description.split(",");
            LocalDate earlierBeginDate = projectInfoMapper.getEarlierBeginDate(codeArray);
            final LocalDateTime createdTime = TimeUtils.workTime(earlierBeginDate.plusDays(1));
            projectMapper.updateCreatedTime(project.getId(),createdTime);
        }
    }
}
