/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package com.schedule.utils;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

/**
 * 分页工具
 *
 * @author Mark
 */
public class PageUtils {
    /**
     * 当前页码
     */
    public static String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static String LIMIT = "limit";

    /**
     * 获取分页起始位置
     */
    public static int getPageOffset(Map<String, Object> params) {
        int curPage = 1;
        int limit = 10;
        if (params.get(PAGE) != null) {
            curPage = Integer.parseInt((String) params.get(PAGE));
        }
        if (params.get(LIMIT) != null) {
            limit = Integer.parseInt((String) params.get(LIMIT));
        }

        return (curPage - 1) * limit;
    }

    /**
     * 获取分页条数
     */
    public static int getPageLimit(Map<String, Object> params) {
        if (params.get(LIMIT) != null) {
            return Integer.parseInt((String) params.get(LIMIT));
        } else {
            return 10;
        }
    }

    public static <T> IPage<T> getPages(Map<String, Object> params) {
        //分页参数
        long curPage = 1;
        long limit = 10;

        if (params.get(PageUtils.PAGE) != null) {
            curPage = Long.parseLong((String) params.get(PageUtils.PAGE));
        }
        if (params.get(PageUtils.LIMIT) != null) {
            limit = Long.parseLong((String) params.get(PageUtils.LIMIT));
        }
        //分页对象
        Page<T> page = new Page<>(curPage, limit);
        return page;
    }
}
