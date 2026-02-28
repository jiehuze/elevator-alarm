package com.schedule.elevator.service.impl;

import com.schedule.elevator.dto.MediaTokenData;
import com.schedule.elevator.service.IMediaPlayService;
import com.schedule.utils.ApiResponseParser;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MediaPlayServiceImpl implements IMediaPlayService {
    private static final String SERVER_URL = "http:/121.26.230.218:18880";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static MediaTokenData TOKEN_DATA = null;

    // 正则匹配data中的code=后字符串
    private static final Pattern CODE_PATTERN = Pattern.compile("code=([a-zA-Z0-9]+)");

    @PostConstruct
    public void init() {
        System.out.println("应用启动时执行 getCode 方法");
        String code = getCode();
        System.out.println("获取到的授权码: " + code);
        TOKEN_DATA = getToken(code);
        System.out.println("获取到的token: " + TOKEN_DATA);
    }

    /**
     * 定时任务：每10分钟执行一次 getCode 方法
     */
    @Scheduled(fixedRate = 600000) // 600000毫秒 = 10分钟
    public void scheduledGetCode() {
        System.out.println("定时任务执行：开始调用 getCode 方法");
        TOKEN_DATA = refreshToken(TOKEN_DATA.getAccessToken(), TOKEN_DATA.getRefreshToken());
        if (TOKEN_DATA == null) {
            System.out.println("定时任务获取到的token为空,重新认证。");
            init();
            return;
        }
        System.out.println("定时任务获取到的授权码: " + TOKEN_DATA);
    }

    /**
     * 从data字符串中提取code值
     *
     * @param data 格式如：http://27.185.65.132?code=a6b02bfb4d2d41afa30cf3cd21a10a23
     * @return code值，如：a6b02bfb4d2d41afa30cf3cd21a10a23
     * @throws IllegalArgumentException 提取失败时抛出
     */
    public String extractAuthCode(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data为空，无法提取code");
        }
        Matcher matcher = CODE_PATTERN.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("data格式错误，未找到code参数：" + data);
        }
    }

    @Override
    public String getCode() {
        // 1. 构建URL参数
        HttpUrl url = HttpUrl.parse(SERVER_URL + "/admin-api/system/oauth2/authorize-sf")
                .newBuilder()
                .addQueryParameter("response_type", "code")
                .addQueryParameter("client_id", "test1")
                .addQueryParameter("redirect_uri", "http://27.185.65.132")
                .build();

        // 2. 构建POST请求（无请求体，参数在URL）
        Request request = new Request.Builder()
                .url(url)
                .addHeader("tenant-id", "1") // 添加请求头
                .post(RequestBody.create(new byte[0], MediaType.parse("application/x-www-form-urlencoded"))) // POST需传空体
                .build();

        // 3. 执行请求并处理响应
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response);
            }
            String jsonResponse = response.body().string();
            String data = ApiResponseParser.extractDataFromJson(jsonResponse, String.class);
            // 返回响应体（授权code通常在响应体/重定向URL中，需根据实际返回格式调整）
            // 5. 提取授权code
            return extractAuthCode(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public MediaTokenData getToken(String code) {
        // 1. 构建请求参数（Postman中body的urlencoded参数）
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("redirect_uri", "http://27.185.65.132");
        params.put("client_secret", "test2");
        params.put("client_id", "test1");

        // 2. 构建form-urlencoded请求体
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBuilder.build();

        // 3. 构建POST请求（匹配Postman的header和url）
        Request request = new Request.Builder()
                .url(SERVER_URL + "/admin-api/system/oauth2/token")
                .addHeader("tenant-id", "1") // Postman中配置的header
                .post(requestBody)
                .build();

        // 4. 执行请求并解析响应
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("HTTP请求失败: " + response.code() + " " + response.message());
                return null;
            }
            String jsonResponse = response.body().string();
            MediaTokenData mediaTokenData = ApiResponseParser.extractDataFromJson(jsonResponse, MediaTokenData.class);

            return mediaTokenData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MediaTokenData refreshToken(String accessToken, String refreshToken) {
        // 1. 构建请求参数（Postman中body的urlencoded参数）
        Map<String, String> params = new HashMap<>();
        params.put("refresh_token", refreshToken);
        params.put("client_id", "test1");
        params.put("client_secret", "test2");
        params.put("grant_type", "refresh_token");

        // 2. 构建form-urlencoded请求体
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBuilder.build();

        // 3. 构建POST请求（匹配Postman的header和url）
        Request request = new Request.Builder()
                .url(SERVER_URL + "/admin-api/system/oauth2/token")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("tenant-id", "1") // Postman中配置的header
                .post(requestBody)
                .build();

        // 4. 执行请求并解析响应
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP请求失败: " + response.code() + " " + response.message());
            }
            String jsonResponse = response.body().string();
            MediaTokenData mediaTokenData = ApiResponseParser.extractDataFromJson(jsonResponse, MediaTokenData.class);

            return mediaTokenData;
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getPlayUrl(String videoId) {
        // 1. 构建请求体（urlencoded参数）
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("video_id", videoId); // 必填参数
        RequestBody requestBody = formBuilder.build();

        // 2. 构建请求（携带Token和tenant-id头）
        Request request = new Request.Builder()
                .url(SERVER_URL + "/admin-api/platform/play/start/" + videoId)
                // 核心请求头：Token认证（Bearer + 空格 + access_token）
                .addHeader("Authorization", "Bearer " + TOKEN_DATA.getAccessToken())
                // 固定头：tenant-id
                .addHeader("tenant-id", "1")
                // 设置Content-Type（OkHttp会自动为FormBody添加，此处显式声明更稳妥）
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .get()
                .build();

        // 3. 执行请求并处理响应
        try (Response response = CLIENT.newCall(request).execute()) {
            // 校验HTTP请求是否成功
            if (!response.isSuccessful()) {
                System.out.println("HTTP请求失败：状态码=" + response.code() + "，原因=" + response.message());
                return null;
            }

            // 读取响应体
            String jsonResponse = response.body().string();
            System.out.println("接口原始返回：" + jsonResponse);

            // 4. 解析响应并提取data（适配多类型）
            return ApiResponseParser.extractDataFromJson(jsonResponse, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
