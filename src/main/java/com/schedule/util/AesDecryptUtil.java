package com.schedule.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesDecryptUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String decrypt(String encryptedData, String captchaCode) throws Exception {
        if (encryptedData == null || encryptedData.isEmpty()) {
            throw new IllegalArgumentException("加密数据不能为空");
        }
        if (captchaCode == null || captchaCode.isEmpty()) {
            throw new IllegalArgumentException("验证码不能为空");
        }

        byte[] keyBytes = deriveKey(captchaCode);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String encrypt(String plainData, String captchaCode) throws Exception {
        if (plainData == null || plainData.isEmpty()) {
            throw new IllegalArgumentException("明文数据不能为空");
        }
        if (captchaCode == null || captchaCode.isEmpty()) {
            throw new IllegalArgumentException("验证码不能为空");
        }

        byte[] keyBytes = deriveKey(captchaCode);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] plainBytes = plainData.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = cipher.doFinal(plainBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static byte[] deriveKey(String captchaCode) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(captchaCode.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest, 0, keyBytes, 0, 16);
        return keyBytes;
    }
}
