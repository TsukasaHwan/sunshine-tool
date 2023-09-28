package org.sunshine.core.tool.util;

import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Teamo
 */
public class DateUtils {

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_DATE = "yyyy-MM-dd";

    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * 老 date 格式化
     */
    public static final ConcurrentDateFormat DATETIME_FORMAT = ConcurrentDateFormat.of(PATTERN_DATETIME);

    public static final ConcurrentDateFormat DATE_FORMAT = ConcurrentDateFormat.of(PATTERN_DATE);

    public static final ConcurrentDateFormat TIME_FORMAT = ConcurrentDateFormat.of(PATTERN_TIME);

    /**
     * java 8 时间格式化
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.PATTERN_DATETIME);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.PATTERN_DATE);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.PATTERN_TIME);

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static Date date() {
        return new Date();
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return formatDateTime(date());
    }

    /**
     * 获取今天的日期<br/>
     * 格式 yyyy-MM-dd
     *
     * @return 时间
     */
    public static String today() {
        return formatDate(date());
    }

    /**
     * 添加年
     *
     * @param date       时间
     * @param yearsToAdd 添加的年数
     * @return 设置后的时间
     */
    public static Date plusYears(Date date, int yearsToAdd) {
        return DateUtils.set(date, Calendar.YEAR, yearsToAdd);
    }

    /**
     * 添加月
     *
     * @param date        时间
     * @param monthsToAdd 添加的月数
     * @return 设置后的时间
     */
    public static Date plusMonths(Date date, int monthsToAdd) {
        return DateUtils.set(date, Calendar.MONTH, monthsToAdd);
    }

    /**
     * 添加周
     *
     * @param date       时间
     * @param weeksToAdd 添加的周数
     * @return 设置后的时间
     */
    public static Date plusWeeks(Date date, int weeksToAdd) {
        return DateUtils.plus(date, Period.ofWeeks(weeksToAdd));
    }

    /**
     * 添加天
     *
     * @param date      时间
     * @param daysToAdd 添加的天数
     * @return 设置后的时间
     */
    public static Date plusDays(Date date, long daysToAdd) {
        return DateUtils.plus(date, Duration.ofDays(daysToAdd));
    }

    /**
     * 添加小时
     *
     * @param date       时间
     * @param hoursToAdd 添加的小时数
     * @return 设置后的时间
     */
    public static Date plusHours(Date date, long hoursToAdd) {
        return DateUtils.plus(date, Duration.ofHours(hoursToAdd));
    }

    /**
     * 添加分钟
     *
     * @param date         时间
     * @param minutesToAdd 添加的分钟数
     * @return 设置后的时间
     */
    public static Date plusMinutes(Date date, long minutesToAdd) {
        return DateUtils.plus(date, Duration.ofMinutes(minutesToAdd));
    }

    /**
     * 添加秒
     *
     * @param date         时间
     * @param secondsToAdd 添加的秒数
     * @return 设置后的时间
     */
    public static Date plusSeconds(Date date, long secondsToAdd) {
        return DateUtils.plus(date, Duration.ofSeconds(secondsToAdd));
    }

    /**
     * 添加毫秒
     *
     * @param date        时间
     * @param millisToAdd 添加的毫秒数
     * @return 设置后的时间
     */
    public static Date plusMillis(Date date, long millisToAdd) {
        return DateUtils.plus(date, Duration.ofMillis(millisToAdd));
    }

    /**
     * 添加纳秒
     *
     * @param date       时间
     * @param nanosToAdd 添加的纳秒数
     * @return 设置后的时间
     */
    public static Date plusNanos(Date date, long nanosToAdd) {
        return DateUtils.plus(date, Duration.ofNanos(nanosToAdd));
    }

    /**
     * 日期添加时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date plus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.plus(amount));
    }

    /**
     * 减少年
     *
     * @param date  时间
     * @param years 减少的年数
     * @return 设置后的时间
     */
    public static Date minusYears(Date date, int years) {
        return DateUtils.set(date, Calendar.YEAR, -years);
    }

    /**
     * 减少月
     *
     * @param date   时间
     * @param months 减少的月数
     * @return 设置后的时间
     */
    public static Date minusMonths(Date date, int months) {
        return DateUtils.set(date, Calendar.MONTH, -months);
    }

    /**
     * 减少周
     *
     * @param date  时间
     * @param weeks 减少的周数
     * @return 设置后的时间
     */
    public static Date minusWeeks(Date date, int weeks) {
        return DateUtils.minus(date, Period.ofWeeks(weeks));
    }

    /**
     * 减少天
     *
     * @param date 时间
     * @param days 减少的天数
     * @return 设置后的时间
     */
    public static Date minusDays(Date date, long days) {
        return DateUtils.minus(date, Duration.ofDays(days));
    }

    /**
     * 减少小时
     *
     * @param date  时间
     * @param hours 减少的小时数
     * @return 设置后的时间
     */
    public static Date minusHours(Date date, long hours) {
        return DateUtils.minus(date, Duration.ofHours(hours));
    }

    /**
     * 减少分钟
     *
     * @param date    时间
     * @param minutes 减少的分钟数
     * @return 设置后的时间
     */
    public static Date minusMinutes(Date date, long minutes) {
        return DateUtils.minus(date, Duration.ofMinutes(minutes));
    }

    /**
     * 减少秒
     *
     * @param date    时间
     * @param seconds 减少的秒数
     * @return 设置后的时间
     */
    public static Date minusSeconds(Date date, long seconds) {
        return DateUtils.minus(date, Duration.ofSeconds(seconds));
    }

    /**
     * 减少毫秒
     *
     * @param date   时间
     * @param millis 减少的毫秒数
     * @return 设置后的时间
     */
    public static Date minusMillis(Date date, long millis) {
        return DateUtils.minus(date, Duration.ofMillis(millis));
    }

    /**
     * 减少纳秒
     *
     * @param date  时间
     * @param nanos 减少的纳秒数
     * @return 设置后的时间
     */
    public static Date minusNanos(Date date, long nanos) {
        return DateUtils.minus(date, Duration.ofNanos(nanos));
    }

    /**
     * 日期减少时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date minus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.minus(amount));
    }

    /**
     * 设置日期属性
     *
     * @param date          时间
     * @param calendarField 更改的属性
     * @param amount        更改数，-1表示减少
     * @return 设置后的时间
     */
    private static Date set(Date date, int calendarField, int amount) {
        Assert.notNull(date, "The date must not be null");
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 日期时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime(Date date) {
        return DATETIME_FORMAT.format(date);
    }

    /**
     * 日期格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * 日期格式化
     *
     * @param date    时间
     * @param pattern 表达式
     * @return 格式化后的时间
     */
    public static String format(Date date, String pattern) {
        return ConcurrentDateFormat.of(pattern).format(date);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime(TemporalAccessor temporal) {
        return DATETIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDate(TemporalAccessor temporal) {
        return DATE_FORMATTER.format(temporal);
    }

    /**
     * java8 时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatTime(TemporalAccessor temporal) {
        return TIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期格式化
     *
     * @param temporal 时间
     * @param pattern  表达式
     * @return 格式化后的时间
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporal);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static Date parse(String dateStr, String pattern) {
        ConcurrentDateFormat format = ConcurrentDateFormat.of(pattern);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param format  ConcurrentDateFormat
     * @return 时间
     */
    public static Date parse(String dateStr, ConcurrentDateFormat format) {
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @param query   移动查询
     * @return 时间
     */
    public static <T> T parse(String dateStr, String pattern, TemporalQuery<T> query) {
        return DateTimeFormatter.ofPattern(pattern).parse(dateStr, query);
    }

    /**
     * 时间转 Instant
     *
     * @param dateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Instant 转 时间
     *
     * @param instant Instant
     * @return Instant
     */
    public static LocalDateTime toDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换成 date
     *
     * @param dateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(DateUtils.toInstant(dateTime));
    }

    /**
     * 转换成 date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts local date time to Calendar.
     *
     * @param localDateTime LocalDateTime
     * @return Calendar
     */
    public static Calendar toCalendar(final LocalDateTime localDateTime) {
        return GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
    }

    /**
     * localDateTime 转换成毫秒数
     *
     * @param localDateTime LocalDateTime
     * @return long
     */
    public static long toMilliseconds(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDate 转换成毫秒数
     *
     * @param localDate LocalDate
     * @return long
     */
    public static long toMilliseconds(LocalDate localDate) {
        return toMilliseconds(localDate.atStartOfDay());
    }

    /**
     * 转换成java8 时间
     *
     * @param calendar 日历
     * @return LocalDateTime
     */
    public static LocalDateTime fromCalendar(final Calendar calendar) {
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    /**
     * 转换成java8 时间
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime fromInstant(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime fromDate(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param milliseconds 毫秒数
     * @return LocalDateTime
     */
    public static LocalDateTime fromMilliseconds(final long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    /**
     * 比较2个时间差，跨度比较小
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 时间间隔
     */
    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    /**
     * 比较2个时间差，跨度比较大，年月日为单位
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Period between(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    /**
     * 比较2个 时间差
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Duration between(Date startDate, Date endDate) {
        return Duration.between(startDate.toInstant(), endDate.toInstant());
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param date      被检查的日期
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 是否在范围内
     */
    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        long beginMills = beginDate.getTime();
        long endMills = endDate.getTime();
        long thisMills = date.getTime();

        return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
    }

    /**
     * 获取某天开始时间
     *
     * @param date Date
     * @return Date
     */
    public static Date beginOfDay(Date date) {
        return toDate(beginOfDay(fromDate(date)));
    }

    /**
     * 获取某天结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date endOfDay(Date date) {
        return toDate(endOfDay(fromDate(date)));
    }

    /**
     * 获取某周开始时间
     *
     * @param date Date
     * @return Date
     */
    public static Date beginOfWeek(Date date) {
        return toDate(beginOfWeek(fromDate(date)));
    }

    /**
     * 获取某周结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date endOfWeek(Date date) {
        return toDate(endOfWeek(fromDate(date)));
    }

    /**
     * 获取某月开始时间
     *
     * @param date Date
     * @return Date
     */
    public static Date beginOfMonth(Date date) {
        return toDate(beginOfMonth(fromDate(date)));
    }

    /**
     * 获取某月结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date endOfMonth(Date date) {
        return toDate(endOfMonth(fromDate(date)));
    }

    /**
     * 获取某年开始时间
     *
     * @param date Date
     * @return Date
     */
    public static Date beginOfYear(Date date) {
        return toDate(beginOfYear(fromDate(date)));
    }

    /**
     * 获取某年结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date endOfYear(Date date) {
        return toDate(endOfYear(fromDate(date)));
    }

    /**
     * 获取指定日期范围
     *
     * @param start 起始日期时间（包括）
     * @param end   结束日期时间
     * @param unit  步进单位 {@link ChronoUnit}
     * @return List<LocalDateTime>
     */
    public static List<Date> range(Date start, Date end, ChronoUnit unit) {
        List<LocalDateTime> range = range(fromDate(start), fromDate(end), unit);
        return range.stream().map(DateUtils::toDate).collect(Collectors.toList());
    }

    /**
     * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int age(Date birthday, Date dateToCompare) {
        Assert.notNull(birthday, "Birthday can not be null !");
        if (null == dateToCompare) {
            dateToCompare = date();
        }
        return age(birthday.getTime(), dateToCompare.getTime());
    }

    /**
     * 获取某天开始时间
     *
     * @param localDateTime LocalDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime beginOfDay(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().atStartOfDay();
    }

    /**
     * 获取某天结束时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime endOfDay(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取某周开始时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime beginOfWeek(LocalDateTime localDateTime) {
        LocalDateTime beginOfWeek = localDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return beginOfDay(beginOfWeek);
    }

    /**
     * 获取某周结束时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime endOfWeek(LocalDateTime localDateTime) {
        LocalDateTime beginOfWeek = localDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return endOfDay(beginOfWeek);
    }

    /**
     * 获取某月开始时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime beginOfMonth(LocalDateTime localDateTime) {
        LocalDateTime beginOfMonth = localDateTime.with(TemporalAdjusters.firstDayOfMonth());
        return beginOfDay(beginOfMonth);
    }

    /**
     * 获取某月结束时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime endOfMonth(LocalDateTime localDateTime) {
        LocalDateTime beginOfMonth = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        return endOfDay(beginOfMonth);
    }

    /**
     * 获取某年开始时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime beginOfYear(LocalDateTime localDateTime) {
        LocalDateTime beginOfYear = localDateTime.with(TemporalAdjusters.firstDayOfYear());
        return beginOfDay(beginOfYear);
    }

    /**
     * 获取某年结束时间
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime endOfYear(LocalDateTime localDateTime) {
        LocalDateTime beginOfYear = localDateTime.with(TemporalAdjusters.lastDayOfYear());
        return endOfDay(beginOfYear);
    }

    /**
     * 获取指定日期范围
     *
     * @param start 起始日期时间（包括）
     * @param end   结束日期时间
     * @param unit  步进单位 {@link ChronoUnit}
     * @return List<LocalDateTime>
     */
    public static List<LocalDateTime> range(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        long between = unit.between(start, end);
        return Stream.iterate(start, date -> unit.addTo(date, 1))
                .limit(between + 1)
                .collect(Collectors.toList());
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param localDateTime 被检查的日期
     * @param begin         起始日期（包含）
     * @param end           结束日期（包含）
     * @return 是否在范围内
     */
    public static boolean isIn(LocalDateTime localDateTime, LocalDateTime begin, LocalDateTime end) {
        return (localDateTime.isAfter(begin) || localDateTime.isEqual(begin)) && (localDateTime.isBefore(end) || localDateTime.isEqual(end));
    }

    /**
     * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int age(LocalDateTime birthday, LocalDateTime dateToCompare) {
        Assert.notNull(birthday, "Birthday can not be null !");
        if (null == dateToCompare) {
            dateToCompare = LocalDateTime.now();
        }
        return age(toMilliseconds(birthday), toMilliseconds(dateToCompare));
    }

    /**
     * 将秒数转换为日时分秒
     *
     * @param seconds 秒数
     * @return 时间
     */
    public static String secondToTime(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be a positive number!");
        }

        long hour = seconds / 3600;
        long other = seconds % 3600;
        long minute = other / 60;
        long second = other % 60;
        final StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }

    /**
     * 计算相对于dateToCompare的年龄，常用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    private static int age(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        final int monthBirth = cal.get(Calendar.MONTH);

        //当前日期，则为0岁
        if (age == 0) {
            return 0;
        } else if (month == monthBirth) {

            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if ((!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth <= dayOfMonthBirth) {
                // 如果生日在当月，但是未超过生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }
}
