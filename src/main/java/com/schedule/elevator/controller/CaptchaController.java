package com.schedule.elevator.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.schedule.common.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sys-user")
public class CaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    private static final Cache<String, String> captchaCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @GetMapping("/captcha")
    public BaseResponse getCaptcha() {
        String captchaText = captchaProducer.createText();
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);

        String captchaKey = UUID.randomUUID().toString();
        captchaCache.put(captchaKey, captchaText);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(captchaImage, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, String> result = new HashMap<>();
            result.put("captchaKey", captchaKey);
            result.put("captchaImage", "data:image/png;base64," + base64Image);

            return new BaseResponse(HttpStatus.OK.value(), "success", result, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "生成验证码失败", null, null);
        }
    }

    public static boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }
        String storedCode = captchaCache.getIfPresent(captchaKey);
        if (storedCode != null && storedCode.equalsIgnoreCase(captchaCode.trim())) {
            captchaCache.invalidate(captchaKey);
            return true;
        }
        return false;
    }
}
