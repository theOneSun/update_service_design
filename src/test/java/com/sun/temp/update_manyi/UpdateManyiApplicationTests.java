package com.sun.temp.update_manyi;

import com.sun.temp.update_manyi.domain.HolidayCalendar;
import com.sun.temp.update_manyi.domain.ProjectTime;
import com.sun.temp.update_manyi.repository.HolidayMapper;
import com.sun.temp.update_manyi.util.HolidayCheck;
import com.sun.temp.update_manyi.util.TimeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UpdateManyiApplicationTests {
    @Autowired
    private HolidayMapper holidayMapper;
    @Autowired
    private HolidayCheck holidayCheck;

    @Test
    void contextLoads() {
    }

    @Test
    void testHolidayFind(){
        final LocalDate localDate = LocalDate.of(2019, 1, 1);
        final HolidayCalendar holidayCalendar = holidayMapper.findByDate(localDate);
        System.out.println(holidayCalendar);
    }

    @Test
    void testFindAllHoliday(){
        final List<LocalDate> allHoliday = holidayMapper.findAllHoliday();
        final LocalDate localDate = LocalDate.of(2019, 1, 2);
        System.out.println(allHoliday.contains(localDate));
    }
    @Test
    void testNotHolidayDate(){
        final LocalDate of = LocalDate.of(2018, 8, 31);
//        final ProjectTime projectTime = new ProjectTime();
//        projectTime.setBeginDate(of);
        final TimeGenerator timeGenerator = new TimeGenerator(new ProjectTime(), holidayCheck);
        timeGenerator.notHolidayDate(of,90,0);
    }
}
