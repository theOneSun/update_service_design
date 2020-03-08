package com.sun.temp.update_manyi.service;

import com.sun.temp.update_manyi.domain.DataUpload;
import com.sun.temp.update_manyi.domain.Project;
import com.sun.temp.update_manyi.domain.ProjectTime;
import com.sun.temp.update_manyi.domain.User;
import com.sun.temp.update_manyi.domain.UserLogin;
import com.sun.temp.update_manyi.repository.ComplexMapper;
import com.sun.temp.update_manyi.util.HolidayCheck;
import com.sun.temp.update_manyi.util.TimeGenerator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author sunjian.
 */
@Service
public class CoreService {

    private final ComplexMapper complexMapper;
    private final HolidayCheck holidayCheck;

    @Autowired
    public CoreService(ComplexMapper complexMapper, HolidayCheck holidayCheck) {
        this.complexMapper = complexMapper;
        this.holidayCheck = holidayCheck;
    }

    //核心方法
    @Transactional
    public void generateDate() {
        /*
        project_time pt;
        project p;
        project_data pd;
        data_upload du;
        user u;
        user_project up;
        user_login ul;
        思路:
        1.读取project_time pt,有开始结束时间的项目都要进行模拟时间
        2.根据 pt 查寻对应的 project 和 user 和data_upload
         */

        //查寻pt
        final List<ProjectTime> allProjectTime = complexMapper.getAllProjectTime();

        List<Project> updateProjectList = new ArrayList<>();
        List<User> updateUserList = new ArrayList<>();
        List<DataUpload> updateDataUploadList = new ArrayList<>();
        List<UserLogin> allUserLoginList = new ArrayList<>();

        for (ProjectTime pt : allProjectTime) {
            //            if (pt.getId().equals("f5848d6d-9a3f-4e0c-9f9c-bcac1bc04781")){
            //                continue;
            //            }
            if (pt.needModify()) {
                final String projectIdStr = pt.getId();
                final UUID projectId = UUID.fromString(projectIdStr);

                //根据项目id查询项目相关的信息
                final Project project = complexMapper.findProjectById(projectId);
                //找创建者和使用者,创建者的创建时间在使用者创建时间之前
                final User creator = complexMapper.findCreator(projectId);
                final List<User> users = complexMapper.findUserByProjectId(projectId);
                //客户用户
                users.remove(creator);
                //数据上传记录
                final List<DataUpload> dataUploadList = complexMapper.findDataUploadByPId(projectId);

                final TimeGenerator timeGenerator = new TimeGenerator(pt, holidayCheck);
                //用户的时间,上传时间直接设置好
                timeGenerator.build(users, dataUploadList);

                //根据timeGenerate修改数据库
                //项目数据
                project.setCreatedDate(timeGenerator.getProjectCreateTime());
                project.setUpdatedDate(timeGenerator.getProjectCreateTime());
                updateProjectList.add(project);

                //项目创建者
                creator.setCreatedDate(timeGenerator.getCreatorTime());
                creator.setUpdatedDate(timeGenerator.getCreatorTime());
                updateUserList.add(creator);
                //用户的创建时间和数据导入时间在timeGenerate中已修改
                updateUserList.addAll(timeGenerator.getUserCreateTimeList());

                updateDataUploadList.addAll(timeGenerator.getDataUploadList());

                //登录时间
                allUserLoginList.addAll(timeGenerator.getUserLoginList());

            }
        }

        //  处理用户,时间保留最早创建时间
        final List<User> handledUpdateUserList = handleUserCreated(updateUserList);

        //处理用户登录记录重复数据
        final List<UserLogin> insertUserLoginList = removeUserLoginDuplicate(allUserLoginList);

        System.out.println("-----update db start-----");
        // 修改数据库
        //1.修改创建用户的创建时间和更新时间
        final int updateUsersResult = complexMapper.updateUsers(handledUpdateUserList);
        System.out.println("更新用户数据条数: " + updateUsersResult);
        System.out.println("更新数量与程序数量对比: " + (updateUsersResult == handledUpdateUserList.size()));
        System.out.println();
        final int updateProjectsResult = complexMapper.updateProjects(updateProjectList);
        System.out.println("更新项目数据条数: " + updateProjectsResult);
        System.out.println("更新数量与程序数量对比: " + (updateProjectsResult == updateProjectList.size()));
        System.out.println();
        final int updateDataUploadsResult = complexMapper.updateDataUploads(updateDataUploadList);
        System.out.println("更新上传数据条数: " + updateDataUploadsResult);
        System.out.println("更新数量与程序数量对比: " + (updateDataUploadsResult == updateDataUploadList.size()));
        System.out.println();
        final int insertUserLoginResult = complexMapper.batchInsertUserLogin(insertUserLoginList);
        System.out.println("更新登录数据条数: " + insertUserLoginResult);
        System.out.println("更新数量与程序数量对比: " + (insertUserLoginResult==insertUserLoginList.size()));
    }

    /**
     * 处理用户创建时间
     *
     * @param updateUserList 更新用户的集合
     * @return 处理后的更新用户数据
     */
    private List<User> handleUserCreated(List<User> updateUserList) {
        List<User> handledList = new ArrayList<>();
        final Map<String, List<User>> map = updateUserList.stream()
                                                          .collect(Collectors.groupingBy(User::getId));

        for (Map.Entry<String, List<User>> entry : map.entrySet()) {
            final List<User> users = entry.getValue();
            final List<User> sortedList = users.stream()
                                               .sorted(Comparator.comparing(User::getCreatedDate))
                                               .collect(Collectors.toList());
            //取最早的时间
            handledList.add(sortedList.get(0));
        }
        return handledList;
    }

    /**
     * 删除重复的userLogin
     * 保留时间最新的
     *
     * @param allUserLoginList 所有的用户登录记录
     * @return 可以插入数据库的记录
     */
    private List<UserLogin> removeUserLoginDuplicate(@NonNull List<UserLogin> allUserLoginList) {
        List<UserLogin> insertList = new ArrayList<>();
        //如果有重复的用户id,保留登录时间最新的
        final Map<String, List<UserLogin>> userLoginMap = allUserLoginList.stream()
                                                                        .collect(Collectors.groupingBy(
                                                                                UserLogin::getUserId));
        for (Map.Entry<String, List<UserLogin>> entry : userLoginMap.entrySet()) {
            final List<UserLogin> loginList = entry.getValue();
            if (loginList.size() == 1) {
                insertList.add(loginList.get(0));
            } else if (loginList.size() > 1) {
                final List<UserLogin> soredList = loginList.stream()
                                                           .sorted(Comparator.comparing(UserLogin::getLastLoginTime))
                                                           .collect(Collectors.toList());
                insertList.add(soredList.get(loginList.size() - 1));
            } else {
                throw new RuntimeException("range error");
            }
        }

        return insertList;
    }

    //生成项目创建时间
    //    private LocalDateTime generateProjectCreatedTime(LocalDateTime creatorTime){
    //
    //    }
    //
    //
    //    /**
    //     * 生成创建者的创建时间和更新时间
    //     *
    //     * @param projectTime 项目开始结束时间信息
    //     */
    //    private LocalDateTime generateCreatorTime(ProjectTime projectTime) {
    //        final LocalDate beginDate = projectTime.getBeginDate();
    //        final LocalDate endDate = projectTime.getEndDate();
    //
    //        //10个工作日内创建用户账号
    //        int day = random.nextInt(10);
    //        LocalDate workDate;
    //        workDate = beginDate.plusDays(day);
    //        //        LocalDateTime createDate = beginDateTime.plusDays(day);
    //
    //        while (holidayCheck.isHoliday(workDate)) {
    //            //如果是节假日就往后顺延两天
    //            workDate = workDate.plusDays(2);
    //        }
    //
    //        if (workDate.isAfter(endDate)){
    //            throw new RuntimeException("创建时间在结束时间之后");
    //        }
    //
    //        //开始日期当天的工作时间开始
    //        final LocalDateTime beginDateTime = workDate.atStartOfDay()
    //                                                    .plus(9, ChronoUnit.HOURS);
    //        //结束时间前一天的下班时间为止
    ////        final LocalDateTime endDateTime = endDate.atStartOfDay()
    ////                                                 .minus(6, ChronoUnit.HOURS);
    //        return workTime(beginDateTime);
    //    }
    //
    //    /**
    //     * 随机工作时间
    //     *
    //     * @param localDateTime 工作的日子
    //     * @return 具体时间
    //     */
    //    private LocalDateTime workTime(@NonNull LocalDateTime localDateTime) {
    //
    //        final int hour = random.nextInt(9);
    //        final int minute = random.nextInt(60);
    //        final int second = random.nextInt(60);
    //        final int milli = random.nextInt(1000);
    //        return localDateTime.plusHours(hour)
    //                            .plusMinutes(minute)
    //                            .plusSeconds(second)
    //                            .plus(milli, ChronoUnit.MILLIS);
    //    }
}
