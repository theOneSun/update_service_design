package com.sun.temp.update_manyi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * @author sunjian.
 */
public class TimeUtils {

    /**
     * 生成一个时间节点
     *
     * @param workDate 工作日日期
     * @return 工作日具体时间
     */
    public static LocalDateTime workTime(LocalDate workDate) {
        Random random = new Random();
        final LocalDateTime workStartTime = workDate.atStartOfDay()
                                                    .plus(9, ChronoUnit.HOURS);
        final int hour = random.nextInt(9);
        final int minute = random.nextInt(60);
        final int second = random.nextInt(60);
        final int milli = random.nextInt(1000);
        return workStartTime.plusHours(hour)
                            .plusMinutes(minute)
                            .plusSeconds(second)
                            .plus(milli, ChronoUnit.MILLIS);

    }
}
