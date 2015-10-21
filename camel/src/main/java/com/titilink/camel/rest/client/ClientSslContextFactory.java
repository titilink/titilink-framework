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
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
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
