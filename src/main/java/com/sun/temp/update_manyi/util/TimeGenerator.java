package com.sun.temp.update_manyi.util;

import com.sun.temp.update_manyi.domain.DataUpload;
import com.sun.temp.update_manyi.domain.ProjectTime;
import com.sun.temp.update_manyi.domain.User;
import com.sun.temp.update_manyi.domain.UserLogin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author sunjian.
 */
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class TimeGenerator {

    @Setter(AccessLevel.NONE)
    private LocalDate beginDate;
    @Setter(AccessLevel.NONE)
    private LocalDate endDate;

    //开始时间
    private LocalDateTime beginTime;
    //结束时间
    private LocalDateTime endTime;
    //项目创建者创建时间
    private LocalDateTime creatorTime;
    //项目创建时间
    private LocalDateTime projectCreateTime;
    //用户创建时间
    //    private LocalDateTime userCreateTime;
    private List<User> userCreateTimeList;

    //项目数据上传时间
    private List<LocalDateTime> uploadTimeList;
    //用户最后一次登录时间
    private LocalDateTime loginTime;
    private List<UserLogin> userLoginList;
    //数据上传时间
    private List<DataUpload> dataUploadList;

    //节假日判断的工具类
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final HolidayCheck holidayCheck;

    private final static LocalDate END_FINAL_DATE = LocalDate.of(2019, 12, 31);
    private final static Random RANDOM = new Random();

    public TimeGenerator(ProjectTime projectTime, HolidayCheck holidayCheck) {
        this.beginDate = projectTime.getBeginDate();
        this.endDate = projectTime.getEndDate();
        this.holidayCheck = holidayCheck;
    }


    public void build(List<User> users, List<DataUpload> uploadList) {
        //        final LocalDate beginDate = projectTime.getBeginDate();
        //        final LocalDate endDate = projectTime.getEndDate();
        //开始日期当天的工作时间开始
        final LocalDateTime beginDateTime = this.beginDate.atTime(9, 0, 0);
        //结束时间前一天的下班时间为止
        LocalDateTime endDateTime;
        if (this.endDate.isBefore(END_FINAL_DATE)) {
            endDateTime = this.endDate.atStartOfDay()
                                      .minus(6, ChronoUnit.HOURS);
        } else {
            endDateTime = END_FINAL_DATE.atTime(6, 0, 0);
        }

        //        final TimeGenerator timeGenerator = new TimeGenerator();
        //开始时间
        this.setBeginTime(beginDateTime);
        //结束时间
        this.setEndTime(endDateTime);

        //创建者时间
        final LocalDateTime creatorTime = this.generateCreatorTime();
        this.setCreatorTime(creatorTime);

        //项目创建时间 创建者创建之后
        final LocalDateTime projectCreatedTime = this.generateProjectTime();
        this.setProjectCreateTime(projectCreatedTime);

        // 用户账号创建时间 项目创建之后 全部完成后需要手动修改蔡梅江账号
        final List<User> updateUsers = generateUserCreateTime(users);
        this.setUserCreateTimeList(updateUsers);
        //        this.setUserCreateTimeList(userCreatedTimeList);

        //数据导入时间,用户创建账号之后
        final List<DataUpload> dataUploads = generateUploadTime(uploadList);
        this.setDataUploadList(dataUploads);

        //使用时间
        final List<UserLogin> loginList = generateLoginTime(users, dataUploads);
        this.setUserLoginList(loginList);
    }

    /**
     * 生成用户登录时间
     *
     * @param users      用户
     * @param uploadList 上传记录
     * @return 登录时间
     */
    private List<UserLogin> generateLoginTime(List<User> users, List<DataUpload> uploadList) {
        /*
        1.根据项目查询所有用户,
        2.用户的登录时间在数据导入时间之后, 在项目结束的1-2月之内,
         */
        List<UserLogin> loginList = new ArrayList<>();
        final DataUpload upload = uploadList.get(uploadList.size() - 1);
        final LocalDate beginDate = upload.getCreatedTime()
                                          .toLocalDate();
        //结束时间延期两个月
        LocalDate endLoginDate = this.getEndTime().toLocalDate()
                                     .plusMonths(2);
        if (endLoginDate.isAfter(END_FINAL_DATE)) {
            endLoginDate = END_FINAL_DATE;
        }

        //日期差
        final long maxOffset = beginDate.until(endLoginDate, ChronoUnit.DAYS);
        for (User user : users) {
            final LocalDate loginDate = notHolidayDate(beginDate, (int) maxOffset/2 + 1, 0);

            if (loginDate.isAfter(endLoginDate)) {
                throw new RuntimeException("用户登录时间超时");
            }

            final LocalDateTime lastLoginTime = workTime(loginDate);
            loginList.add(new UserLogin(UUID.randomUUID().toString(), user.getId(), lastLoginTime));
        }

        return loginList;
    }

    /**
     * 生成上传时间
     *
     * @param uploadList 上传记录
     */
    private List<DataUpload> generateUploadTime(List<DataUpload> uploadList) {
        //开始时间,在项目创建时间之后
        final LocalDateTime beginTime = this.getProjectCreateTime();
        //结束时间,项目结束日期之前
        final LocalDateTime endTime = this.getEndTime();
        //日期差
        final long maxOffset = beginTime.toLocalDate()
                                        .until(endTime.toLocalDate(), ChronoUnit.DAYS);
        final LocalDate createDate = notHolidayDate(beginTime.toLocalDate(), (int) maxOffset/2 + 1, 0);

        if (createDate.isAfter(endTime.toLocalDate())) {
            throw new RuntimeException("创建时间在结束时间之后");
        }

        final LocalDateTime workTime = workTime(createDate);
        for (DataUpload upload : uploadList) {
            //升序每次多个几分钟
            final LocalDateTime uploadTime = workTime.plusMinutes(RANDOM.nextInt(10))
                                                     .plusSeconds(RANDOM.nextInt(60))
                                                     .plus(RANDOM.nextInt(1000), ChronoUnit.MILLIS);
            upload.setCreatedTime(uploadTime);
        }
        return uploadList;
    }

    /**
     * 生成用户账号的创建时间
     *
     * @param users 用户
     */
    private List<User> generateUserCreateTime(List<User> users) {
        //开始时间,在项目创建时间之后
        final LocalDateTime beginTime = this.getProjectCreateTime();
        //结束时间,项目结束日期之前
        final LocalDateTime endTime = this.getEndTime();
        //日期差
        final long maxOffset = beginTime.toLocalDate()
                                        .until(endTime.toLocalDate(), ChronoUnit.DAYS);
        final LocalDate createDate = notHolidayDate(beginTime.toLocalDate(), (int) maxOffset/2 + 1, 0);

        if (createDate.isAfter(endTime.toLocalDate())) {
            throw new RuntimeException("创建时间在结束时间之后");
        }

        final LocalDateTime workTime = workTime(createDate);
        for (User user : users) {
            user.setCreatedDate(workTime);
            user.setUpdatedDate(workTime);
        }
        return users;
    }


    /**
     * 生成项目创建时间
     * 在创建用户创建之后,项目结束之前
     *
     * @return 项目创建时间
     */
    private LocalDateTime generateProjectTime() {
        //项目创建的最早时间,在创建用户的创建日期之后
        final LocalDateTime beginTime = this.getCreatorTime();
        //项目创建的最晚时间
        final LocalDateTime endTime = this.getEndTime();

        //日期差
        final long maxOffset = beginTime.toLocalDate()
                                        .until(endTime.toLocalDate(), ChronoUnit.DAYS);

        //项目创建的日期
        final LocalDate createDate = notHolidayDate(beginTime.toLocalDate(), (int) maxOffset/2 + 1, 0);

        if (createDate.isAfter(endTime.toLocalDate())) {
            throw new RuntimeException("创建时间在结束时间之后");
        }

        return workTime(createDate);
    }

    /**
     * 生成一个时间节点
     *
     * @param workDate 工作日日期
     * @return 工作日具体时间
     */
    private LocalDateTime workTime(LocalDate workDate) {
        final LocalDateTime workStartTime = workDate.atStartOfDay()
                                                    .plus(9, ChronoUnit.HOURS);
        final int hour = RANDOM.nextInt(9);
        final int minute = RANDOM.nextInt(60);
        final int second = RANDOM.nextInt(60);
        final int milli = RANDOM.nextInt(1000);
        return workStartTime.plusHours(hour)
                            .plusMinutes(minute)
                            .plusSeconds(second)
                            .plus(milli, ChronoUnit.MILLIS);

    }

    /**
     * 生成一个在期间的工作日日期
     *
     * @param beginDate 开始日期
     * @param maxOffset 最大偏移量
     * @param loopCount 循环次数
     * @return 随机工作日日期
     */
    public LocalDate notHolidayDate(LocalDate beginDate, int maxOffset, int loopCount) {
        LocalDate result;
        final int offset = RANDOM.nextInt(maxOffset);
        result = beginDate.plusDays(offset);
        final boolean holiday = this.holidayCheck.isHoliday(result);
        /*while ( ) {
            notHolidayDate(beginDate, maxOffset, ++loopCount);
            if (loopCount == maxOffset || loopCount > 20) {
                throw new RuntimeException("达到最大循环次数,项目期间的节假日有点多");
            }
        }*/
        if (holiday) {
            if (loopCount == maxOffset*2 || loopCount > 20) {
                throw new RuntimeException("达到最大循环次数,项目期间的节假日有点多");
            }
            notHolidayDate(beginDate, maxOffset, ++loopCount);
        }
        return result;
    }

    /**
     * 生成创建者的创建时间和更新时间
     */
    private LocalDateTime generateCreatorTime() {

        //3个工作日内创建用户账号
        final int day = RANDOM.nextInt(3);
        LocalDate workDate;
        workDate = this.beginDate.plusDays(day);
        //        LocalDateTime createDate = beginDateTime.plusDays(day);

        while (this.holidayCheck.isHoliday(workDate)) {
            //如果是节假日就往后顺延两天
            workDate = workDate.plusDays(2);
        }

        /*if (workDate.isAfter(this.endDate)) {
            throw new RuntimeException("创建时间在结束时间之后");
        }*/

        return workTime(workDate);
    }

    /**
     * 随机工作时间
     *
     * @param localDateTime 工作的日子
     * @return 具体时间
     */
    @Deprecated
    private LocalDateTime workTime(@NonNull LocalDateTime localDateTime) {
        final int hour = RANDOM.nextInt(9);
        final int minute = RANDOM.nextInt(60);
        final int second = RANDOM.nextInt(60);
        final int milli = RANDOM.nextInt(1000);
        return localDateTime.plusHours(hour)
                            .plusMinutes(minute)
                            .plusSeconds(second)
                            .plus(milli, ChronoUnit.MILLIS);
    }

}
