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
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
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
