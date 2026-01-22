/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.schedule.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 日期处理工具类
 *
 * @author Mark
 * @since 1.0.0
 */
@Slf4j
public class DateUtils {
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_PATTERN_CHINA = "yyyy年MM月dd日";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);

            return df.format(localDateTime);
        }
        return null;
    }

    /**
     * 格式化LocalDateTime为指定格式，如果为null则返回空字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(df);
    }


    /**
     * 日期解析
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回Date
     */
    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            log.error("日期解析异常,date:{},pattern:{},message:{}", date, pattern, e.getMessage(), e);
        }
        return null;
    }

    public static String RandomStr() {
        // 创建一个 Random 对象
        Random random = new Random();

        // 生成两个随机字母
        char firstLetter = (char) ('A' + random.nextInt(26));
        char secondLetter = (char) ('A' + random.nextInt(26));

        // 组合成字符串
        String twoLetterString = "" + firstLetter + secondLetter;

        return twoLetterString;
    }

    public static LocalDate calculateCountDown(LocalDate deadline, int countDownType) {
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline cannot be null");
        }

        switch (countDownType) {
            case 1:
                // 三个月后到期 → 减去 3 个月
                return deadline.minus(3, ChronoUnit.MONTHS);
            case 2:
                // 一个月后到期 → 减去 1 个月
                return deadline.minus(1, ChronoUnit.MONTHS);
            case 3:
                // 半个月后到期 → 减去 15 天
                return deadline.minus(15, ChronoUnit.DAYS);
            case 4:
                // 一周后到期 → 减去 7 天
                return deadline.minus(7, ChronoUnit.DAYS);
            default:
                throw new IllegalArgumentException("Invalid countDownType: " + countDownType);
        }
    }

    /**
     * 计算两个时间点之间的时间差（秒）
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间差（秒），如果结束时间早于开始时间则返回负数
     */
    public static long calculateTimeDifferenceInSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.Duration.between(start, end).getSeconds();
    }

    /**
     * 根据年份获取每个月及季度的第一天和最后一天时间
     *
     * @param year 年份
     * @return Map<Integer, LocalDateTime [ ]> - key为标识(1-12表示月份，13-15表示季度)，value为[开始时间, 结束时间]数组
     * 月份：1-12分别代表1月到12月
     * 季度：13-15分别代表Q1-Q3 (按顺序：1月2月3月一季度，4月5月6月二季度，7月8月9月三季度，10月11月12月四季度)
     */
    public static Map<Integer, LocalDateTime[]> getMonthlyAndQuarterlyRanges(int year) {
        Map<Integer, LocalDateTime[]> ranges = new HashMap<>();

        // 按顺序添加每月时间范围：1月，2月，3月，一季度，4月，5月，6月，二季度，7月，8月，9月，三季度，10月，11月，12月，四季度
        for (int month = 1; month <= 12; month++) {
            // 获取当月第一天的开始时间 (00:00:00)
            LocalDate firstDay = LocalDate.of(year, month, 1);
            LocalDateTime startOfMonth = firstDay.atStartOfDay(); // 00:00:00

            // 获取当月最后一天的结束时间 (23:59:59)
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate lastDay = yearMonth.atEndOfMonth();
            LocalDateTime endOfMonth = lastDay.atTime(23, 59, 59); // 23:59:59

            ranges.put(month, new LocalDateTime[]{startOfMonth, endOfMonth});

            // 在特定月份后添加对应季度
            if (month == 3) { // 一季度（1-3月）在3月后添加
                ranges.put(13, new LocalDateTime[]{ranges.get(1)[0], ranges.get(3)[1]});
            } else if (month == 6) { // 二季度（4-6月）在6月后添加
                ranges.put(14, new LocalDateTime[]{ranges.get(4)[0], ranges.get(6)[1]});
            } else if (month == 9) { // 三季度（7-9月）在9月后添加
                ranges.put(15, new LocalDateTime[]{ranges.get(7)[0], ranges.get(9)[1]});
            } else if (month == 12) { // 四季度（10-12月）在12月后添加
                ranges.put(16, new LocalDateTime[]{ranges.get(10)[0], ranges.get(12)[1]});
            }
        }

        return ranges;
    }

}
