package com.titancore.framework.common.properties;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用于执行 AES 加密和解密操作的 Java 工具类
 */
public class AesUtils {
    //指定了算法、分组模式和填充方式。
    public static final String CONTENT_CIPHER_ALGORITHM = "AES/CTR/NoPadding";

    /**
     * ：对 AES 加密的数据进行解密
     * @param key AES 密钥
     * @param iv 初始化向量。
     * @param data 要解密的数据。
     * @return
     */
    @SneakyThrows //注解用于在方法中处理异常，它可以让你在不显式捕获异常的情况下抛出异常。
    public static byte[] decrypt(byte[] key, byte[] iv, byte[] data) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(CONTENT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    /**
     *  对数据进行 AES 加密。
     * @param key AES 密钥。
     * @param iv 初始化向量。
     * @param data ：要加密的数据。
     * @return
     */
    @SneakyThrows
    public static byte[] encrypt(byte[] key, byte[] iv, byte[] data) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(CONTENT_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

}
