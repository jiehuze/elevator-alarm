package com.schedule.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.elevator.dto.ApiResponse;

public class ApiResponseParser {
    // 全局ObjectMapper（单例，避免重复创建）
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 解析接口响应字符串
     *
     * @param jsonResponse 接口返回的JSON字符串
     * @return 解析后的CommonApiResponse
     * @throws Exception 解析失败时抛出
     */
    public static ApiResponse parseResponse(String jsonResponse) throws Exception {
        return OBJECT_MAPPER.readValue(jsonResponse, ApiResponse.class);
    }

    /**
     * 提取并转换data字段（根据实际类型适配）
     *
     * @param response 已解析的响应对象
     * @param clazz    目标类型（如TokenData.class/String.class）
     * @param <T>      泛型，兼容不同类型
     * @return 转换后的data对象（null则返回null）
     */
    public static <T> T extractData(ApiResponse response, Class<T> clazz) {
        Object data = response.getData();
        if (data == null) {
            return null;
        }

        // 1. 如果data本身就是目标类型，直接返回
        if (clazz.isInstance(data)) {
            return clazz.cast(data);
        }

        // 2. 如果是其他类型（如LinkedHashMap，Jackson默认解析JSON对象为Map），转换为目标类型
        try {
            // 将data转换为JSON字符串，再反序列化为目标类型
            String dataJson = OBJECT_MAPPER.writeValueAsString(data);
            return OBJECT_MAPPER.readValue(dataJson, clazz);
        } catch (Exception e) {
            throw new RuntimeException("转换data类型失败：" + e.getMessage(), e);
        }
    }

    /**
     * 快捷方法：直接从JSON字符串提取指定类型的data（code=0时）
     *
     * @param jsonResponse 接口返回的JSON字符串
     * @param clazz        目标类型
     * @param <T>          泛型
     * @return 转换后的data
     * @throws Exception 解析/业务失败时抛出
     */
    public static <T> T extractDataFromJson(String jsonResponse, Class<T> clazz) throws Exception {
        ApiResponse response = parseResponse(jsonResponse);

        // 先判断业务状态码
        if (response.getCode() != 0) {
            throw new RuntimeException("接口调用失败：" + response.getMsg() + "（code=" + response.getCode() + "）");
        }

        return extractData(response, clazz);
    }
}
