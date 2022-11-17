package com.ocs.busi.helper;

import com.ocs.common.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tangyixiang
 * @Date 2022/11/8
 */
public class ExcelCellHelper {

    public static LocalDateTime handleDate(String excelCellDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(excelCellDate, dateTimeFormatter);
        return localDateTime;
    }

}
