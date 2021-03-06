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

import com.titilink.camel.rest.common.AdapterRestletUtil;
import com.titilink.camel.rest.common.RestletServerCall;
import com.titilink.common.log.AppLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.engine.adapter.HttpServerHelper;
import org.restlet.engine.adapter.ServerCall;
import org.restlet.representation.Representation;

import java.io.InputStream;
import java.util.Arrays;

/**
 * 将Restlet的Response转化为Netty的响应
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class RestletServerHelper extends HttpServerHelper {

    private static final AppLogger LOGGER = AppLogger.getInstance(RestletServerHelper.class);

    public static final int BUFFER_SIZE = 1024;

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_LENGTH = "Content-Length";

    public RestletServerHelper(Server server) {
        super(server);
    }

    private ByteBuf transferBuffer(Representation represent) {

        if (null == represent) {
            return null;
        }

        InputStream in = null;
        try {
            in = represent.getStream();
            byte[] content = new byte[0];
            byte[] readbytes = new byte[BUFFER_SIZE];
            int length = -1;
            while ((length = in.read(readbytes, 0, BUFFER_SIZE)) != -1) {
                int preLen = content.length;
                content = Arrays.copyOf(content, preLen + length);
                System.arraycopy(readbytes, 0, content, preLen, length);
            }

            ByteBuf buf = Unpooled.copiedBuffer(content, 0, content.length);
            return buf;
        } catch (Exception e) {
            LOGGER.error("Failed to read buffer from result entity", e);
            return null;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e) {
                    LOGGER.error("Failed to close io stream", e);
                }
            }
        }
    }

    public void handle(ServerCall httpCall) {
        LOGGER.debug("handle httpCall begin.");

        try {
            // ----------- netty to restlet 数据 转换点  开始--------------------
            RestletServerCall retHttpCall = (RestletServerCall) httpCall;
            //这里将HttpCall中的HTTP版本传给了header头部信息，但没有修改到request的getProtocol中，原因是org.restlet.Request.getProtocol()中Protocol为空，通过url的scheme获取了默认的HTTP Protocol 1.1
            HttpRequest restletRequest = getAdapter().toRequest(httpCall);
            restletRequest.setProtocol(httpCall.getProtocol());

            DefaultFullHttpRequest nettyRequest = (DefaultFullHttpRequest) ((RestletServerCall) httpCall).getRequest();
            nettyRequest.content();
            // ----------- netty to restlet 数据 转换点  结束--------------------

            HttpResponse response = new HttpResponse(httpCall, restletRequest);
            LOGGER.debug("before handle request by restlet");
            handle(restletRequest, response);
            LOGGER.debug("after handle request by restlet");
            //封装response为HttpCore的HttpResponse对象，同时将值返回回去
            Representation represent = response.getEntity();
            ByteBuf buf = transferBuffer(represent);
            FullHttpResponse nettyResponse =
                    new DefaultFullHttpResponse(retHttpCall.getRequest().getProtocolVersion(),
                            HttpResponseStatus.valueOf(response.getStatus().getCode()));

            //Copy响应的头部信息
            AdapterRestletUtil.parseToNettyFullResponse(retHttpCall, response, nettyResponse);
            if (null != buf) {
                nettyResponse.content().writeBytes(buf);
                buf.release();
            } else {
                LOGGER.error("buf onf content is null.");
            }

            nettyResponse.headers().set(CONTENT_TYPE, MediaType.APPLICATION_JSON);
            nettyResponse.headers().set(CONTENT_LENGTH, nettyResponse.content().readableBytes());

            if (null != retHttpCall.getCtx()) {
                final Channel channel = retHttpCall.getCtx().channel();
                if (channel != null) {
                    if (channel.isActive()) {
                        channel.writeAndFlush(nettyResponse);
                    } else {
                        LOGGER.error("channel not active,close it.");
                        channel.close();
                    }
                } else {
                    LOGGER.error("Write to channel failed, invalid channel");
                }
            } else {
                LOGGER.error("retHttpCall.getCtx() is null.");
            }
        } catch (Throwable e) {
            LOGGER.error("Exception while handle request:", e);
            if (httpCall instanceof RestletServerCall) {
                RestletServerCall retHttpCall = (RestletServerCall) httpCall;
                if (retHttpCall.getCtx() != null) {
                    final Channel channel = retHttpCall.getCtx().channel();
                    if (channel != null) {
                        LOGGER.error("Close channel.");
                        channel.close();
                    }
                }
            }
        } finally {
            LOGGER.debug("handle httpCall end.");
            Engine.clearThreadLocalVariables();
        }
    }

}
