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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

}
