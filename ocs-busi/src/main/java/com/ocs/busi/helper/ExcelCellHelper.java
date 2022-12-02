package com.ocs.busi.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
