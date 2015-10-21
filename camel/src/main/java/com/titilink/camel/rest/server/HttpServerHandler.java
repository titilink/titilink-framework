/**
 * 项目名称: titilink
 * 文件名称: HttpServerHandler.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.common.log.AppLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final AppLogger LOGGER = AppLogger.getInstance(HttpServerHandler.class);

    private boolean isSSL = false;

    public HttpServerHandler(boolean isSSL) {
        this.isSSL = isSSL;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        LOGGER.debug("channelRead0 begin.");

        //将HttpRequest和HttpResponse转化为Restlet
        RestletServerWrapper wrapper = RestletServerWrapperMgt.getDefaultWrapper(isSSL);
        if (null != wrapper) {
            wrapper.service(ctx, request);
        } else {
            LOGGER.error("wrapper is null, please check ");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOGGER.error("Exception caught:", cause);
        ctx.channel().close();
    }

}
