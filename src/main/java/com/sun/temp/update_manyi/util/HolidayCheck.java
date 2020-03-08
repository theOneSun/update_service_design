package com.sun.temp.update_manyi.util;

import com.sun.temp.update_manyi.repository.HolidayMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * @author sunjian.
 */
@Component
public class HolidayCheck {

    private final HolidayMapper holidayMapper;

    private List<LocalDate> holidays = null;

    @Autowired
    public HolidayCheck(HolidayMapper holidayMapper) {
        this.holidayMapper = holidayMapper;
    }

    /**
     * 判断是否是节假日
     *
     * @param localDate 要判断的日期
     * @return true:节假日;false:工作日
     */
    public boolean isHoliday(@NonNull LocalDate localDate) {
        if (ObjectUtils.isEmpty(holidays)) {
            setHolidays(this.holidayMapper.findAllHoliday());
        }
        return holidays.contains(localDate);
    }

    private void setHolidays(@NonNull List<LocalDate> list) {
        this.holidays = list;
    }
}
