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

import com.titilink.common.log.AppLogger;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 请求初始化
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final AppLogger LOGGER = AppLogger.getInstance(HttpClientInitializer.class);

    private final boolean ssl;

    public HttpClientInitializer(boolean ssl) {
        LOGGER.info("isSSL:{}", ssl);
        this.ssl = ssl;
    }

    @Override
    public void initChannel(SocketChannel ch)
            throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

//        p.addLast("log", new LoggingHandler(LogLevel.INFO));
        // Enable HTTPS if necessary.
        if (ssl) {
            SSLEngine engine = ClientSslContextFactory.getClientContext().createSSLEngine();
            engine.setUseClientMode(true);

            p.addLast("ssl", new SslHandler(engine));
        }

        p.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast("aggregator", new HttpObjectAggregator(1048576));

        p.addLast("handler", new HttpClientHandler());
    }

}
