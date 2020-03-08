package com.sun.temp.update_manyi.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author sunjian.
 */
@Data
public class HolidayCalendar {
    private UUID id;
    private LocalDate standardDate;
}
