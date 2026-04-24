package com.schedule.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.common.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 外部API调用鉴权拦截器
 * 鉴权方式：Header + Bearer Token
 *
 * 请求头传递：
 * X-Timestamp: 当前时间戳（毫秒）
 * X-Api-Key: 分配的 API Key
 * Authorization: Bearer <signature>
 *
 * signature = HMAC-SHA256(timestamp + apiKey, secretKey)
 *
 * 服务端校验步骤：
 * 1. 从请求头获取 timestamp、apiKey、Bearer Token（signature）
 * 2. 校验 apiKey 是否匹配
 * 3. 校验 timestamp 是否在有效时间窗口内（5分钟）
 * 4. 使用相同的算法重新计算签名，校验 signature 是否一致
 */
@Component
public class ExternalApiInterceptor implements HandlerInterceptor {

    /**
     * 有效时间窗口（毫秒），5分钟
     */
    private static final long VALID_TIME_WINDOW = 5 * 60 * 1000L;

    /**
     * Bearer 前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY = "96365-OKC-7G2T9P5XK8ZQ4L7S2D9N";
    private static final String SECRET_KEY = "K9cS7pR2tF5gH1jQ";
    private static final Boolean authEnabled = true;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("[ExternalApiInterceptor] 拦截到请求：" + request.getRequestURI() + "，authEnabled=" + authEnabled + "，configuredApiKey=" + API_KEY);

        // 如果未启用鉴权，直接放行
        if (!authEnabled) {
            System.out.println("[ExternalApiInterceptor] 鉴权未启用，直接放行");
            return true;
        }

        // 从请求头获取参数
        String timestamp = request.getHeader("X-Timestamp");
        String apiKey = request.getHeader("X-Api-Key");
        String authHeader = request.getHeader("Authorization");

        // 校验必要参数是否存在
        if (timestamp == null || apiKey == null) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "缺少鉴权参数，请传递X-Timestamp和X-Api-Key请求头");
            return false;
        }

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "缺少鉴权信息，请在Authorization头中传递Bearer Token");
            return false;
        }

        String signature = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (signature.isEmpty()) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token不能为空");
            return false;
        }

        // 校验 API Key
        if (!API_KEY.equals(apiKey)) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "无效的API Key");
            return false;
        }

        // 校验时间戳有效性（防重放攻击）
        long requestTime;
        try {
            requestTime = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "时间戳格式错误");
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - requestTime) > VALID_TIME_WINDOW) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "请求已过期，时间戳超出有效范围");
            return false;
        }

        // 校验签名
        String expectedSignature = generateSignature(timestamp, API_KEY, SECRET_KEY);
        if (!expectedSignature.equals(signature)) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "签名验证失败");
            return false;
        }

        System.out.println("[ExternalApiInterceptor] 鉴权通过");
        return true;
    }

    /**
     * 生成签名（即 Bearer Token 的值）
     *
     * @param timestamp 时间戳
     * @param apiKey    API Key
     * @param secretKey 密钥
     * @return 十六进制签名字符串
     */
    public static String generateSignature(String timestamp, String apiKey, String secretKey) {
        try {
            String message = timestamp + apiKey;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        BaseResponse baseResponse = new BaseResponse(status, message, null, null);
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    }

    /**
     * 测试入口：生成签名
     */
    public static void main(String[] args) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String apiKey = "96365-OKC-7G2T9P5XK8ZQ4L7S2D9N";
        String secretKey = "K9cS7pR2tF5gH1jQ";
        String signature = generateSignature(timestamp, apiKey, secretKey);
        System.out.println("X-Timestamp: " + timestamp);
        System.out.println("X-Api-Key: " + apiKey);
        System.out.println("Authorization: Bearer " + signature);
    }
}
