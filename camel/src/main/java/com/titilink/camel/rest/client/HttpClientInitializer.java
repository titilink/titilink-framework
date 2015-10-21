/**
 * 项目名称: titilink
 * 文件名称: HttpClientInitializer.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
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
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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
