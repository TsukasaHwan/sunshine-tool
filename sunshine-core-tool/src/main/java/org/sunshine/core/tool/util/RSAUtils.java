package org.sunshine.core.tool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Teamo
 * @since 2023/01/09
 */
public class RSAUtils {

    private static final Logger log = LoggerFactory.getLogger(RSAUtils.class);

    private static final String ALGORITHM = "RSA";

    private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----\n";

    private static final String PUBLIC_KEY_SUFFIX = "\n-----END PUBLIC KEY-----";

    private static final String PRIVATE_KEY_PREFIX = "-----BEGIN PRIVATE KEY-----\n";

    private static final String PRIVATE_KEY_SUFFIX = "\n-----END PRIVATE KEY-----";

    private static final String PUBLIC_FILE_NAME = "app.pub";

    private static final String PRIVATE_FILE_NAME = "app.key";

    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final int keySize;

    private RSAUtils(int keySize) {
        this.keySize = keySize;
    }

    /**
     * 创建RSA工具类，默认密钥长度为2048
     *
     * @return RSA工具类
     */
    public static RSAUtils create() {
        return create(2048);
    }

    /**
     * 创建RSA工具类
     *
     * @param keySize 密钥长度
     * @return RSA工具类
     */
    public static RSAUtils create(int keySize) {
        return new RSAUtils(keySize);
    }

    /**
     * 随机生成密钥对
     *
     * @param filePath 生成的文件路径
     */
    public void genKeyPair(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("生成密钥失败", e);
            return;
        }
        // 初始化密钥对生成器
        keyPairGen.initialize(keySize, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String publicKeyString = formatKeyString(
                Base64.getEncoder().encodeToString(publicKey.getEncoded()),
                PUBLIC_KEY_PREFIX, PUBLIC_KEY_SUFFIX
        );
        String privateKeyString = formatKeyString(
                Base64.getEncoder().encodeToString(privateKey.getEncoded()),
                PRIVATE_KEY_PREFIX, PRIVATE_KEY_SUFFIX
        );

        Path publicPath = Paths.get(filePath, PUBLIC_FILE_NAME);
        Path privatePath = Paths.get(filePath, PRIVATE_FILE_NAME);
        try {
            Files.createDirectories(Paths.get(filePath));
            Files.writeString(publicPath, publicKeyString);
            Files.writeString(privatePath, privateKeyString);
        } catch (IOException e) {
            log.error("密钥写入文件失败", e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    private String formatKeyString(String key, String prefix, String suffix) {
        String formatted = key.replaceAll("(.{64})", "$1\n");
        // 确保末尾没有多余换行
        formatted = formatted.replaceAll("\n$", "");
        return prefix + formatted + suffix;
    }
}
