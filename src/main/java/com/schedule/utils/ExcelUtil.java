/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.schedule.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.schedule.excel.FormTemplateExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {


    public static boolean isExcelFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        // 检查文件名是否以.xls或.xlsx结尾
        return "xls".equalsIgnoreCase(extension)
                || "xlsx".equalsIgnoreCase(extension);
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response    response
     * @param fileName    文件名
     * @param sheetName   sheetName
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static void exportExcelToTarget(HttpServletResponse response, String fileName, String sheetName, List<?> sourceList,
                                           Class<?> targetClass) throws Exception {
        List targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            Object target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        exportExcel(response, fileName, sheetName, targetList, targetClass);
    }

    /**
     * Excel导出
     *
     * @param response  response
     * @param fileName  文件名
     * @param sheetName sheetName
     * @param list      数据List
     * @param pojoClass 对象Class
     */
    public static void exportExcel(HttpServletResponse response, String fileName, String sheetName, List<?> list,
                                   Class<?> pojoClass) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            //当前日期
            fileName = DateUtils.format(new Date());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), pojoClass).registerConverter(new LongStringConverter()).sheet(sheetName).doWrite(list);
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response    response
     * @param fileName    文件名
     * @param sheetName   sheetName
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static void exportExcelToTargetWithTemplate(HttpServletResponse response, String fileName, String sheetName, List<?> sourceList,
                                                       Class<?> targetClass, String templateFileName) throws Exception {
        List targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            Object target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        exportExcelWithTemplate(response, fileName, sheetName, targetList, targetClass, templateFileName);
    }

    /**
     * Excel导出
     *
     * @param response  response
     * @param fileName  文件名
     * @param sheetName sheetName
     * @param list      数据List
     * @param pojoClass 对象Class
     */
    public static void exportExcelWithTemplate(HttpServletResponse response, String fileName, String sheetName, List<?> list,
                                               Class<?> pojoClass, String templateFileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            //当前日期
            fileName = DateUtils.format(new Date());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 创建字体对象并设置大小
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 16); // 设置字体大小为12磅

        // 创建内容样式并关联字体
        WriteCellStyle contentStyle = new WriteCellStyle();
        contentStyle.setWriteFont(font);
        contentStyle.setBorderLeft(BorderStyle.THIN);      // 左边框
        contentStyle.setBorderRight(BorderStyle.THIN);     // 右边框
        contentStyle.setBorderTop(BorderStyle.THIN);       // 上边框
        contentStyle.setBorderBottom(BorderStyle.THIN);    // 下边框
        contentStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());  // 边框颜色
        contentStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        contentStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        contentStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(null, contentStyle);

        Resource resource = new ClassPathResource(templateFileName);
        try (InputStream inputStream = resource.getInputStream()) {
            // 使用EasyExcel写入数据
            EasyExcel.write(response.getOutputStream(), pojoClass)
                    .registerWriteHandler(styleStrategy)
//                    .registerWriteHandler(new CustomMergeStrategy(list))
                    .withTemplate(inputStream)
                    .sheet()
                    .needHead(false)
                    .doWrite(list);
        }
    }

    /**
     * Excel导出
     *
     * @param response  response
     * @param fileName  文件名
     * @param sheetName sheetName
     * @param list      数据List
     * @param pojoClass 对象Class
     */
    public static void exportExcelWithTemplate2(HttpServletResponse response, String fileName, String sheetName, List<FormTemplateExcel> list,
                                               Class<?> pojoClass, String templateFileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            //当前日期
            fileName = DateUtils.format(new Date());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 创建字体对象并设置大小
//        WriteFont font = new WriteFont();
//        font.setFontHeightInPoints((short) 16); // 设置字体大小为12磅
//
//        // 创建内容样式并关联字体
//        WriteCellStyle contentStyle = new WriteCellStyle();
//        contentStyle.setWriteFont(font);
//        contentStyle.setBorderLeft(BorderStyle.THIN);      // 左边框
//        contentStyle.setBorderRight(BorderStyle.THIN);     // 右边框
//        contentStyle.setBorderTop(BorderStyle.THIN);       // 上边框
//        contentStyle.setBorderBottom(BorderStyle.THIN);    // 下边框
//        contentStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());  // 边框颜色
//        contentStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        contentStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        contentStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(null, contentStyle);

        Resource resource = new ClassPathResource(templateFileName);
        try (InputStream inputStream = resource.getInputStream()) {
            // 使用EasyExcel写入数据
            EasyExcel.write(response.getOutputStream(), pojoClass)
//                    .registerWriteHandler(styleStrategy)
                    .registerWriteHandler(new CustomMergeStrategy(list))
                    .withTemplate(inputStream)
                    .sheet()
                    .needHead(false)
                    .doWrite(list);
        }
    }
}
