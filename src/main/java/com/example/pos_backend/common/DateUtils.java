package com.example.pos_backend.common;

import com.example.pos_backend.constants.GlobalConstants;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * 日期时间工具类
 * 提供日期时间相关的常用操作方法
 */
public class DateUtils {

    /**
     * 默认时区
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    /**
     * UTC 时区
     */
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(GlobalConstants.DateTimeFormat.DATE);

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern(GlobalConstants.DateTimeFormat.DATETIME);

    /**
     * 时间戳格式化器
     */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern(GlobalConstants.DateTimeFormat.TIMESTAMP);

    /**
     * 时间格式化器
     */
    private static final DateTimeFormatter TIME_FORMATTER = 
            DateTimeFormatter.ofPattern(GlobalConstants.DateTimeFormat.TIME);

    /**
     * 私有构造函数，防止实例化
     */
    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate nowDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalTime nowTime() {
        return LocalTime.now();
    }

    /**
     * 将字符串转换为 LocalDateTime
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime 对象
     * @throws DateTimeParseException 解析异常
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 将字符串转换为 LocalDate
     *
     * @param dateStr 日期字符串
     * @return LocalDate 对象
     * @throws DateTimeParseException 解析异常
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 将字符串转换为 LocalTime
     *
     * @param timeStr 时间字符串
     * @return LocalTime 对象
     * @throws DateTimeParseException 解析异常
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    /**
     * 将字符串转换为 Instant
     *
     * @param timestampStr 时间戳字符串
     * @return Instant 对象
     * @throws DateTimeParseException 解析异常
     */
    public static Instant parseInstant(String timestampStr) {
        if (timestampStr == null || timestampStr.trim().isEmpty()) {
            return null;
        }
        return Instant.parse(timestampStr);
    }

    /**
     * 格式化 LocalDateTime 为字符串
     *
     * @param dateTime LocalDateTime 对象
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化 LocalDate 为字符串
     *
     * @param date LocalDate 对象
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 格式化 LocalTime 为字符串
     *
     * @param time LocalTime 对象
     * @return 格式化后的字符串
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }

    /**
     * 格式化 Instant 为字符串
     *
     * @param instant Instant 对象
     * @return 格式化后的字符串
     */
    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(UTC_ZONE_ID).format(TIMESTAMP_FORMATTER);
    }

    /**
     * 将 LocalDateTime 转换为 Instant
     *
     * @param dateTime LocalDateTime 对象
     * @return Instant 对象
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant();
    }

    /**
     * 将 Instant 转换为 LocalDateTime
     *
     * @param instant Instant 对象
     * @return LocalDateTime 对象
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
    }

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date Date 对象
     * @return LocalDateTime 对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param dateTime LocalDateTime 对象
     * @return Date 对象
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 判断日期是否在指定范围内
     *
     * @param date  要检查的日期
     * @param start 开始日期
     * @param end   结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * 判断日期时间是否在指定范围内
     *
     * @param dateTime 要检查的日期时间
     * @param start    开始日期时间
     * @param end      结束日期时间
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (dateTime == null || start == null || end == null) {
            return false;
        }
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }

    /**
     * 计算两个时间戳之间的秒数
     *
     * @param start 开始时间戳
     * @param end   结束时间戳
     * @return 秒数差
     */
    public static long secondsBetween(Instant start, Instant end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 获取一天的开始时间
     *
     * @param date 日期
     * @return 一天的开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * 获取一天的结束时间
     *
     * @param date 日期
     * @return 一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 获取一个月的开始日期
     *
     * @param date 日期
     * @return 月初日期
     */
    public static LocalDate startOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }

    /**
     * 获取一个月的结束日期
     *
     * @param date 日期
     * @return 月末日期
     */
    public static LocalDate endOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * 判断是否为今天
     *
     * @param date 日期
     * @return 是否为今天
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }

    /**
     * 判断是否为工作日（周一到周五）
     *
     * @param date 日期
     * @return 是否为工作日
     */
    public static boolean isWeekday(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    /**
     * 判断是否为周末
     *
     * @param date 日期
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDate date) {
        return !isWeekday(date);
    }
}
