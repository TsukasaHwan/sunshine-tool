package org.sunshine.core.tool.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Teamo
 */
public class DateUtils extends DateUtil {

    public static final FastDateFormat YEAR_MOTH_FORMAT = FastDateFormat.getInstance("yyyy-MM");

    public static final FastDateFormat SLASH_DATE_FORMAT = FastDateFormat.getInstance("yyyy/MM/dd");

    /**
     * 根据季度获取日期
     *
     * @param quarter 季度
     * @return 日期列表
     */
    public static List<String> getDateQuarter(int quarter) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        switch (quarter) {
            case 1:
                start.set(Calendar.MONTH, 0);
                start.set(Calendar.DATE, 1);
                end.set(Calendar.MONTH, 2);
                end.set(Calendar.DATE, 31);
                break;
            case 2:
                start.set(Calendar.MONTH, 3);
                start.set(Calendar.DATE, 1);
                end.set(Calendar.MONTH, 5);
                end.set(Calendar.DATE, 30);
                break;
            case 3:
                start.set(Calendar.MONTH, 6);
                start.set(Calendar.DATE, 1);
                end.set(Calendar.MONTH, 8);
                end.set(Calendar.DATE, 30);
                break;
            case 4:
                start.set(Calendar.MONTH, 9);
                start.set(Calendar.DATE, 1);
                end.set(Calendar.MONTH, 11);
                end.set(Calendar.DATE, 31);
                break;
            default:
                return null;
        }
        List<DateTime> dateTimes = rangeToList(beginOfDay(start.getTime()), endOfDay(end.getTime()), DateField.MONTH);
        return dateTimes.stream().map(dateTime -> dateTime.toString(YEAR_MOTH_FORMAT)).collect(Collectors.toList());
    }
}
