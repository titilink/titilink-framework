/**
 * 项目名称: titilink
 * 文件名称: HttpServerInitializer.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;

/**
 * ChannelPipeline初始化
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 通道是否支持SSL
     */
    private final boolean supportSSL;

    public HttpServerInitializer(boolean supportSSL) {
        this.supportSSL = supportSSL;
    }

    @Override
    public void initChannel(SocketChannel ch)
            throws Exception {
        //默认的ChannelPipeline
        ChannelPipeline pipeline = ch.pipeline();

        if (isSupportSSL()) {
            SSLEngine engine = ServerSslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        //设置Protocol编码解码器
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(196608));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        pipeline.addLast("handler", new HttpServerHandler(supportSSL));
    }

    public boolean isSupportSSL() {
        return supportSSL;
    }

}
