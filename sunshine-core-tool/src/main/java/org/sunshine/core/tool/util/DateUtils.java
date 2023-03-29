package org.sunshine.core.tool.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Teamo
 */
public class DateUtils extends DateUtil {
    public static final String PATTERN_YEAR_MONTH = "yyyy-MM";

    /**
     * 根据年份，周获取当前周的开始日期和结束日期
     *
     * @param year
     * @param week
     * @return
     */
    public static String getWeekDays(final int year, final int week) {
        Calendar cal = calendar();
        // 设置每周的开始日期
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String beginDate = format(cal.getTime(), DatePattern.NORM_DATE_FORMAT);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = format(cal.getTime(), DatePattern.NORM_DATE_FORMAT);
        return beginDate + StringPool.TILDA + endDate;
    }

    /**
     * 根据剩余秒倒计时
     *
     * @return 返回格式 HH:mm:ss
     */
    public static String countDown(long second) {
        if (second > 0) {
            long hh = second / 60 / 60 % 60;
            long mm = second / 60 % 60;
            long ss = second % 60;
            return String.format("%02d", hh) + StringPool.COLON + String.format("%02d", mm) + StringPool.COLON + String.format("%02d", ss);
        }
        return StringPool.EMPTY;
    }

    /**
     * 根据季度获取日期
     *
     * @param quarter
     * @return
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
        return dateTimes.stream().map(dateTime -> dateTime.toString(PATTERN_YEAR_MONTH)).collect(Collectors.toList());
    }
}
