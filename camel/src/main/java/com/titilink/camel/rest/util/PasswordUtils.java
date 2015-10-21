/**
 * Copyright 2005-2015 titilink
 *
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 *
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 *
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 *
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 *
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 *
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 *
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 *
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 *
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.camel.rest.util;

import com.titilink.common.app.AppProperties;
import com.titilink.common.log.AppLogger;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * 加解密工具类
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class PasswordUtils {

    private static final AppLogger LOGGER = AppLogger.getInstance(PasswordUtils.class);

    /**
     * 十进制16
     */
    private static final int DECIMAL_16 = 16;

    /**
     * 迭代次数,最小50000
     */
    private static final int ITERATION_COUNT = 50000;

    /**
     * AES加密算法的长度
     */
    private static final int AES_KEY_LEN = 128;

    /**
     * 默认的key_pass值
     */
    private static final String DEFAULT_KEY_PASS = "OMM@123";

    /**
     * 默认的salt值
     */
    private static final String DEFAULT_SALT = "y5x6G+jW9w==";

    /**
     * 默认的IV向量字符串
     */
    private static final String DEFAULT_IV = "U56ClHWVWbdw5PljZtGNpQ==";

    /**
     * string编码格式
     */
    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * AES加密算法
     */
    public static final String ENCODER_AES = "AES";

    /**
     * SHA-256加密算法
     */
    public static final String ENCODER_SHA256 = "SHA-256";

    /**
     * 增强AES加密算法
     */
    public static final String ENCODER_MUTIL = "AES/CBC/PKCS5Padding";

    /**
     * SecureRandom算法
     */
    public static final String ALGORITHM_SHA1PRNG = "SHA1PRNG";

    /**
     * IV向量字节数组的最小长度
     */
    private static final int DECIMAL_8 = 8;

    /**
     * 密钥对象
     */
    private static Key skeySpec = null;

    /**
     * 向量对象
     */
    private static IvParameterSpec ivpspec = null;

    /**
     * 构造方法，用于消除checkstyle: 工具类使用私有构造方法，可以禁止实例化
     */
    private PasswordUtils() {
        // NOP
    }

    /**
     * 初始化参数，检查参数，获取密钥对象，加载向量
     *
     * @param content 待加密或解密的内容
     * @param ivparam 向量字节数组
     */
    private static void initParams(String content, byte[] ivparam) {
        checkParams(content, ivparam);

        if (null == skeySpec) {
            loadKey();
        }

        ivpspec = new IvParameterSpec(ivparam);
    }

    /**
     * 通过Rabiit和salt可以导出AES加密的密钥，也可以通过该算法进行单向口令加密，比SHA256更可靠.
     *
     * @param Rabiit 密钥生成参数或者待加密的内容
     * @param salt   盐值
     * @return 密钥对象
     */
    public synchronized static Key generateKey(char[] Rabiit, byte[] salt) {
        SecretKeyFactory factory;
        SecretKey tmpkey = null;
        SecretKey secret = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            //AES加密密钥的长度，默认为128，支持配置。密钥长度决定了加密算法类型是AES128还是其它类型。
            int aeskeylen = AppProperties.getAsInt("AES_KEY_LEN", AES_KEY_LEN);
            KeySpec keyspec = new PBEKeySpec(Rabiit, salt, ITERATION_COUNT, aeskeylen);
            tmpkey = factory.generateSecret(keyspec);

            //对密钥对象使用AES算法重新包装，否则会加解密失败
            secret = new SecretKeySpec(tmpkey.getEncoded(), ENCODER_AES);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("generateKey error, no such method exception.");
        } // "PBKDF2WithHmacSHA256"   在JDK8里面支持
        catch (InvalidKeySpecException e) {
            LOGGER.error("generateKey error, invalid key exception.");
        }
        return secret;
    }

    /**
     * 通过密钥对象的字节数组还原成名密钥对象
     *
     * @param secRetKeyByte 密钥对象字节数组
     * @param algorithm     算法名称
     * @return Key 密钥对象
     */
    private static Key generateKey(byte[] secRetKeyByte, String algorithm) {
        return new SecretKeySpec(secRetKeyByte, algorithm);
    }

    /**
     * 加载密钥
     */
    private synchronized static void loadKey() {
        try {
            if (null != skeySpec) {
                return;
            }
            String keyPass = AppProperties.get("KEY_PASS", DEFAULT_KEY_PASS);
            byte[] salt = Base64.decodeBase64(DEFAULT_SALT);
            skeySpec = generateKey(keyPass.toCharArray(), salt);
        } catch (Throwable t) {
            LOGGER.error("loadKey error.");
        }
    }

    /**
     * 通过明文和密钥使用AES加密算法得到密文
     *
     * @param plainText     明文
     * @param secRetKeyByte 密钥字节数组
     * @return String 密文
     */
    public synchronized static String aesEncrypt(String plainText, byte[] secRetKeyByte) {
        checkParams(plainText, secRetKeyByte);
        Key key = generateKey(secRetKeyByte, ENCODER_AES);
        String encryptResult = null;
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_AES);
            byte[] byteContent = plainText.getBytes(ENCODING_UTF8);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            encryptResult = Base64.encodeBase64String(result);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("aesEncrypt error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("aesEncrypt error, no such padding exception");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("aesEncrypt error, unsupported encoding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("aesEncrypt error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("aesEncrypt error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("aesEncrypt error, bad padding exception");
        }

        return encryptResult;
    }

    /**
     * 根据密文和密钥对象数组使用AES算法进行解密
     *
     * @param ciphertext    密文
     * @param secRetKeyByte 密钥字节数组
     * @return String 明文
     */
    public synchronized static String aesDecrpt(String ciphertext, byte[] secRetKeyByte) {
        //参数检查
        checkParams(ciphertext, secRetKeyByte);

        String decryptResult = null;
        Key key = generateKey(secRetKeyByte, ENCODER_AES);
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_AES);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            decryptResult = new String(result, ENCODING_UTF8);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("aesDecrpt error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("aesDecrpt error, no such padding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("aesDecrpt error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("aesDecrpt error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("aesDecrpt error, bad padding exception");
        } catch (IOException e) {
            LOGGER.error("aesDecrpt error, io exception");
        }
        return decryptResult;
    }

    /**
     * 使用默认的向量进行加密，建议不要使用默认的向量
     *
     * @param content 明文
     * @return 密文
     */
    public synchronized static String encryptByAes(String content) {
        byte[] defaultiv = Base64.decodeBase64(DEFAULT_IV);
        return encryptByAes(content, defaultiv);
    }

    /**
     * 使用默认密钥进行AES加密
     *
     * @param content 需要加密的内容
     * @param ivparam 向量字节数组
     * @return 密文
     */
    public synchronized static String encryptByAes(String content, byte[] ivparam) {
        initParams(content, ivparam);
        return encryptByAes(content, skeySpec, ivpspec);
    }

    /**
     * 使用指定密钥进行AES加密
     *
     * @param content 明文
     * @param key     密钥
     * @param ivp     向量对象
     * @return String 密文
     */
    private static String encryptByAes(String content, Key key, IvParameterSpec ivp) {
        String encryptResult = "";
        if (null == content) {
            return encryptResult;
        }
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_MUTIL);
            byte[] byteContent = content.getBytes(ENCODING_UTF8);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key, ivp);
            byte[] result = cipher.doFinal(byteContent);
            encryptResult = Base64.encodeBase64String(result);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encryptByAes error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("encryptByAes error, no such padding exception");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("encryptByAes error, unsupported encoding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("encryptByAes error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("encryptByAes error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("encryptByAes error, bad padding exception");
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("encryptByAes error, invalid algorithm parameter exception");
        }
        return encryptResult;

    }

    /**
     * 使用默认的向量解密，建议不要使用默认的向量
     *
     * @param content 密文
     * @return 明文
     */
    public synchronized static String decryptByAes(String content) {
        byte[] defaultiv = Base64.decodeBase64(DEFAULT_IV);
        return decryptByAes(content, defaultiv);
    }

    /**
     * 解密方法
     *
     * @param content 密文
     * @param ivparam 向量字节数组
     * @return String
     * 明文
     */
    public synchronized static String decryptByAes(String content, byte[] ivparam) {
        initParams(content, ivparam);
        return decryptByAes(content, skeySpec, ivpspec);
    }

    /**
     * 用指定密钥AES解密
     *
     * @param content 待解密内容
     * @param key     密钥
     * @param ivp     向量对象
     * @return 明文
     */
    public synchronized static String decryptByAes(String content, Key key, IvParameterSpec ivp) {
        // 解密结果
        String decryptResult = "";
        if (null == content) {
            return decryptResult;
        }
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_MUTIL);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key, ivp);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            decryptResult = new String(result, ENCODING_UTF8);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("decryptByAes error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("decryptByAes error, no such padding exception");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("decryptByAes error, unsupported encoding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("decryptByAes error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("decryptByAes error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("decryptByAes error, bad padding exception");
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("decryptByAes error, invalid algorithm parameter exception");
        }
        return decryptResult;
    }

    /**
     * SHA256算法加密
     *
     * @param content 待加密内容
     * @return 密文
     */
    public synchronized static String encryptBySHA256(String content) {
        // 加密结果
        String encryptResultStr = "";
        if (null == content) {
            return encryptResultStr;
        }
        try {
            // 生成MessageDigest对象,传入所用算法的参数(SHA-1)
            MessageDigest msgDigest = MessageDigest.getInstance(ENCODER_SHA256);
            // 使用 getBytes( )方法生成字符串数组
            msgDigest.update(content.getBytes(ENCODING_UTF8));
            // 执行MessageDigest对象的digest( )方法完成计算，计算的结果通过字节类型的数组返回
            byte[] digesta = msgDigest.digest();
            encryptResultStr = OtherUtil.parseByte2HexStr(digesta);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encryptBySHA256 error, no such algorithm exception");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("encryptBySHA256 error, unsupported encoding exception");
        }

        return encryptResultStr;
    }

    /**
     * 使用SecureRandom生成十六位的随机数
     *
     * @return 返回随机数字节数
     */
    public synchronized static byte[] generateSecRamdom() {
        try {
            SecureRandom sr = SecureRandom.getInstance(ALGORITHM_SHA1PRNG);
            byte[] bytes = new byte[DECIMAL_16];
            sr.nextBytes(bytes);
            return bytes;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("generateSecRamdom error, no such algorithm exception");
        }
        return null;
    }

    /**
     * 参数检查，不合法抛出异常
     *
     * @param content 待加密或者解密的内容
     * @param ivparam 字节数组
     */
    private static void checkParams(String content, byte[] ivparam) {
        if ((null == content) || (content.isEmpty())) {
            throw new RuntimeException("the content to encrpt is null or empty");
        }

        if ((null == ivparam) || (ivparam.length < DECIMAL_8)) {
            throw new RuntimeException("byte[] is null or less than eight bytes");
        }
    }

    /**
     * 通过明文和密钥使用AES加密算法得到密文
     *
     * @param plainText     明文
     * @param secRetKeyByte 密钥字节数组
     * @return String 密文
     */
    public synchronized static String aesEncryptSpec(String plainText, byte[] secRetKeyByte) {
        checkParams(plainText, secRetKeyByte);
        Key key = generateKey(secRetKeyByte, ENCODER_AES);
        String encryptResult = null;
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_AES);
            byte[] byteContent = plainText.getBytes(ENCODING_UTF8);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            encryptResult = OtherUtil.parseByte2HexStr(result);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("aesEncryptSpec error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("aesEncryptSpec error, no such padding exception");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("aesEncryptSpec error, unsupported encoding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("aesEncryptSpec error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("aesEncryptSpec error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("aesEncryptSpec error, bad padding exception");
        }

        return encryptResult;
    }

    /**
     * 根据密文和密钥对象数组使用AES算法进行解密
     *
     * @param ciphertext    密文
     * @param secRetKeyByte 密钥字节数组
     * @return String 明文
     */
    public synchronized static String aesDecrptSpec(String ciphertext, byte[] secRetKeyByte) {
        //参数检查
        checkParams(ciphertext, secRetKeyByte);

        String decryptResult = null;
        Key key = generateKey(secRetKeyByte, ENCODER_AES);
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCODER_AES);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(OtherUtil.parseHexStr2Byte(ciphertext));
            decryptResult = new String(result);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("aesDecrptSpec error, no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("aesDecrptSpec error, no such padding exception");
        } catch (InvalidKeyException e) {
            LOGGER.error("aesDecrptSpec error, invalid key exception");
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("aesDecrptSpec error, illegal block size exception");
        } catch (BadPaddingException e) {
            LOGGER.error("aesDecrptSpec error, bad padding exception");
        }
        return decryptResult;
    }

}
