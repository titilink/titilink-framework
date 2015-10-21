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
package com.titilink.common.app;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * 加解密工具类
 *
 * @author kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class EncryptDecryptUtil {

    public void testMD5() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        byte[] retArr =
            md.digest("this is a security string".getBytes(Charset.forName("UTF-8")));
        System.out.println(Hex.encodeHexString(retArr));

        byte[] retArr1 =
                md.digest("this is a security string".getBytes(Charset.forName("UTF-8")));
        System.out.println(Hex.encodeHexString(retArr1));

        byte[] retArr2 =
                md.digest("this is a security string.".getBytes(Charset.forName("UTF-8")));
        System.out.println(Hex.encodeHexString(retArr2));

        byte[] retArr3 =
                md.digest("this is a security string.".getBytes(Charset.forName("UTF-8")));
        System.out.println(Hex.encodeHexString(retArr3));
    }

    public void testBASE64ByJDK() throws IOException {
        //初始化base64加密器
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //base64加密
        String cipherText = base64Encoder.encode("this is a security string from server"
                .getBytes(Charset.forName("UTF-8")));

        //初始化base64解密器
        BASE64Decoder base64Decoder = new BASE64Decoder();
        //base64解密
        byte[] retArr = base64Decoder.decodeBuffer(cipherText);
        System.out.println(new String(retArr, Charset.forName("UTF-8")));
    }

    public void testBASE64ByApache() throws UnsupportedEncodingException {
        //使用apache common codec加密数据
        byte[] cipherText = Base64.encodeBase64("this is a security string from server".getBytes("UTF-8"));
        //使用apache common codec解密数据
        byte[] plainData = Base64.decodeBase64(cipherText);
        System.out.println(new String(plainData, Charset.forName("UTF-8")));
    }

    public void testDES() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        //创建加解密密钥
        DESKeySpec desKeySpec = new DESKeySpec("SECURITY".getBytes(Charset.forName("UTF-8")));
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

        //服务端使用对称密钥加密数据
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
        byte[] cipherData = cipher.doFinal("this is a security text from server"
                .getBytes(Charset.forName("UTF-8")));

        //客户端使用对称密钥解密数据
        Cipher cipher1 = Cipher.getInstance("DES");
        cipher1.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
        byte[] plainData = cipher1.doFinal(cipherData);
        System.out.println(new String(plainData, Charset.forName("UTF-8")));
    }

    public void testRSA() throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            SignatureException {
        //创建密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        //创建公钥私钥
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        //服务端使用私钥加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey, new SecureRandom());
        byte[] cipherData = cipher.doFinal("this is a security text from server"
                .getBytes(Charset.forName("UTF-8")));

        //客户端使用公钥解密
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, publicKey, new SecureRandom());
        byte[] plainData = cipher1.doFinal(cipherData);
        System.out.println(new String(plainData, Charset.forName("UTF-8")));

        //服务端根据私钥和加密数据生成签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(cipherData);
        byte[] signData = signature.sign();

        //客户端根据公钥和加密数据验证数据是否被修改
        Signature signature1 = Signature.getInstance("MD5withRSA");
        signature1.initVerify(publicKey);
        signature1.update(cipherData);
        System.out.println(signature1.verify(signData));

    }

}
