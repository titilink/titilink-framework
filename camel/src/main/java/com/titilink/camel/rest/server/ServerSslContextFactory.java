/**
 * 项目名称: titilink
 * 文件名称: ServerSslContextFactory.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.camel.rest.util.SecureChatKeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.*;

/**
 * <pre>
 * 用于Server端的SslContext</br>
 * 加载JKS key store</br>
 * 如果仅用于Client端，请使用{@link com.titilink.camel.rest.client.ClientSslContextFactory}
 * </pre>
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class ServerSslContextFactory {

    private static final String PROTOCOL = "TLS";

    private static final SSLContext SERVER_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            //如果没有指定ssl keystore type,默认使用sun jre,如果使用其他jre,该值会在jre启动的时候设置
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(SecureChatKeyStore.asInputStream(), SecureChatKeyStore.getKeyStorePassword());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, SecureChatKeyStore.getCertificatePassword());

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (KeyManagementException e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        } catch (UnrecoverableKeyException e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        } catch (KeyStoreException e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        } catch (RuntimeException e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    private ServerSslContextFactory() {

    }

}
