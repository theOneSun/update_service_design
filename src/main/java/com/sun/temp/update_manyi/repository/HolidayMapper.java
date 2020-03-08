package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.HolidayCalendar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface HolidayMapper {
    HolidayCalendar findByDate(@Param("date")LocalDate localDate);

    List<LocalDate> findAllHoliday();
}
