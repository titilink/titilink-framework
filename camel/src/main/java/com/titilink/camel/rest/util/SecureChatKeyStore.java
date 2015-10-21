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

import com.titilink.camel.rest.common.AdapterRestletUtil;
import com.titilink.common.log.AppLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A bogus key store which provides all the required information to
 * create an example SSL connection.
 * <p>
 * To generate a bogus key store:
 * <pre>
 * keytool  -genkey -alias securechat -keysize 2048 -validity 36500
 *          -keyalg RSA -dname "CN=securechat"
 *          -keypass secret -storepass secret
 *          -keystore cert.jks
 * </pre>
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class SecureChatKeyStore {

    private static final String SERVER_REST_SSL_CERTPASS = "server.rest.ssl.certpass";

    private static final String SERVER_REST_SSL_KEYSTOREPASS = "server.rest.ssl.keystorepass";

    /**
     * certpass密文的IV向量字符串
     */
    private static final String CERT_PASS_IV = "8FF3BD6756B7F943EB27818FD8404B47";

    /**
     * keystorepass密文的IV向量字符串
     */
    private static final String KEY_STORE_PASS_IV = "C0286CB5FA911112B8972010D0BFDE46";

    private static final String DEFAULT_KEY_STORE = "config" + File.separator + "camel.jks";

    private static final String SERVER_REST_SSL_KEYSTORE = "server.rest.ssl.keystore";

    private static final AppLogger LOGGER = AppLogger.getInstance(SecureChatKeyStore.class);

    public static InputStream asInputStream()
            throws FileNotFoundException {
        String filePath = AdapterRestletUtil.getProperty(SERVER_REST_SSL_KEYSTORE, DEFAULT_KEY_STORE);
        File jksFile = new File(filePath);
        if (jksFile.exists()) {
            return new FileInputStream(jksFile);
        } else {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        }
    }

    public static char[] getCertificatePassword() {
        String certpass = null;
        try {
            certpass =
                    PasswordUtils.decryptByAes(AdapterRestletUtil.getProperty(SERVER_REST_SSL_CERTPASS),
                            OtherUtil.parseHexStr2Byte(CERT_PASS_IV));
        } catch (Throwable e) {
            LOGGER.error("certpass error.");
        }
        return (certpass == null) ? null : certpass.toCharArray();
    }

    public static char[] getKeyStorePassword() {
        String keystorepass = null;
        try {
            keystorepass =
                    PasswordUtils.decryptByAes(AdapterRestletUtil.getProperty(SERVER_REST_SSL_KEYSTOREPASS),
                            OtherUtil.parseHexStr2Byte(KEY_STORE_PASS_IV));
        } catch (Throwable e) {
            LOGGER.error("keystorepass error.");
        }
        return (keystorepass == null) ? null : keystorepass.toCharArray();
    }

    private SecureChatKeyStore() {
        // Unused
    }

}
