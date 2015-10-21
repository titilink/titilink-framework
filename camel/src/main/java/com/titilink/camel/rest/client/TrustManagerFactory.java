/**
 * 项目名称: titilink
 * 文件名称: TrustManagerFactory.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.client;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

/**
 * Bogus {@link javax.net.ssl.TrustManagerFactorySpi} which accepts any certificate
 * even if it is invalid.
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class TrustManagerFactory extends TrustManagerFactorySpi {

    private static final TrustManager DUMMY_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {

        }
    };

    public static TrustManager[] getTrustManagers() {
        return new TrustManager[]{DUMMY_TRUST_MANAGER};
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return getTrustManagers();
    }

    @Override
    protected void engineInit(KeyStore keystore)
            throws KeyStoreException {

    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
            throws InvalidAlgorithmParameterException {

    }

}
