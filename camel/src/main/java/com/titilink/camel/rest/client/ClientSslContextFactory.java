/**
 * 项目名称: titilink
 * 文件名称: ClientSslContextFactory.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.client;

import java.security.*;

import javax.net.ssl.*;

/**
 * Creates a bogus {@link SSLContext}.  A client-side context created by this
 * factory accepts any certificate even if it is invalid.  A server-side context
 * created by this factory sends a bogus certificate defined in {@link com.titilink.camel.rest.util.SecureChatKeyStore}.
 * <p>
 * You will have to create your context differently in a real world application.
 * <p>
 * <h3>Client Certificate Authentication</h3>
 * <p>
 * To enable client certificate authentication:
 * <ul>
 * <li>Enable client authentication on the server side by calling
 * {@link javax.net.ssl.SSLEngine#setNeedClientAuth(boolean)} before creating
 * {@link io.netty.handler.ssl.SslHandler}.</li>
 * <li>When initializing an {@link SSLContext} on the client side,
 * specify the {@link javax.net.ssl.KeyManager} that contains the client certificate as
 * the first argument of {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)}.</li>
 * <li>When initializing an {@link SSLContext} on the server side,
 * specify the proper {@link TrustManager} as the second argument of
 * {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)}
 * to validate the client certificate.</li>
 * </ul>
 * <p>
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public final class ClientSslContextFactory {

    private static final String PROTOCOL = "TLS";

    private static final SSLContext CLIENT_CONTEXT;

    static {
        SSLContext clientContext;

        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, TrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    private ClientSslContextFactory() {

    }

}
