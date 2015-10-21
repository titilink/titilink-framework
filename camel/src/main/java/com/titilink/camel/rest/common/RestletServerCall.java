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
package com.titilink.camel.rest.common;

import com.titilink.common.app.AppProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.adapter.ServerCall;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 重写restlet 的ServletCall
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class RestletServerCall extends ServerCall {

    private volatile HttpRequest request;

    private volatile HttpResponse response;

    private volatile Series<Header> requestHeaders;

    private volatile ChannelHandlerContext ctx;

    private static final int HTTP_PORT = AppProperties.getAsInt("server.rest.http.port", 80);

    private static final int HTTPS_PORT = AppProperties.getAsInt("server.rest.https.port", 443);

    public static final int HOST_SPLIT_LIMIT = 2;

    /**
     * 获取netty request
     *
     * @return
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * 获取netty response
     *
     * @return
     */
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * 构造器，增强Restlet ServerCall，加入netty的context、requset、response
     *
     * @param ctx
     * @param server
     * @param request
     * @param response
     */
    public RestletServerCall(ChannelHandlerContext ctx, Server server,
                             HttpRequest request, HttpResponse response) {
        super(server);
        this.request = request;
        this.response = response;
        this.ctx = ctx;
    }

    @Override
    public boolean abort() {
        return false;
    }

    @Override
    public String getClientAddress() {
        return null;
    }

    @Override
    public int getClientPort() {
        return HTTP_PORT;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public InputStream getRequestEntityStream(long size) {
        ByteBuf buf = ((FullHttpRequest) request).content();

        // notice:new byte 只能用到int的size，因此在netty配置中，不能让传进来的内容大小超过int最大范围
        byte[] allBytes = new byte[(int) size];
        ByteBuffer[] allBufs = buf.nioBuffers();
        ByteArrayInputStream result = null;
        int index = 0;
        if (null != allBufs) {
            for (ByteBuffer oneBuf : allBufs) {
                if (null != oneBuf) {
                    System.arraycopy(oneBuf.array(), 0, allBytes, index, oneBuf.capacity());
                    index += oneBuf.capacity();
                }
            }
        }
        result = new ByteArrayInputStream(allBytes);
        return result;
    }

    @Override
    public Series<Header> getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.requestHeaders = new Series<Header>(Header.class);

            // Copy the headers from the request object
            String headerName;
            String headerValue;

            DefaultFullHttpRequest requestLocal = (DefaultFullHttpRequest) getRequest();

            HttpHeaders headers = requestLocal.headers();
            if (null != headers) {
                for (Map.Entry<String, String> entry : headers.entries()) {
                    headerName = entry.getKey();
                    headerValue = entry.getValue();
                    this.requestHeaders.add(headerName, headerValue);
                }
            }
        }

        return this.requestHeaders;
    }

    /**
     * 设置Restlet Request Header
     *
     * @param requestHeaders
     */
    public void setRequestHeaders(Series<Header> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    @Override
    public InputStream getRequestHeadStream() {
        return null;
    }

    @Override
    public String getRequestUri() {
        return getRequest().getUri();
    }

    @Override
    public OutputStream getResponseEntityStream() {
        return null;
    }

    @Override
    public String getServerAddress() {
        String[] result = HttpHeaders.getHost(getRequest()).split(":", HOST_SPLIT_LIMIT);
        if (result.length > 0) {
            return result[0];
        }
        return null;
    }

    @Override
    public int getServerPort() {
        String[] result = HttpHeaders.getHost(getRequest()).split(":", HOST_SPLIT_LIMIT);
        if (result.length > 1) {
            return Integer.parseInt(result[1]);
        }
        //equals包装了equalsIgnoreCase
        if (Protocol.HTTPS.equals(getRequest().getProtocolVersion().protocolName())) {
            return HTTPS_PORT;
        }
        return HTTP_PORT;
    }

    @Override
    public String getVersion() {
        String result = null;
        final String protocolVersion = getRequest().getProtocolVersion().toString();
        final int index = protocolVersion.indexOf('/');

        if (index != -1) {
            result = protocolVersion.substring(index + 1);
        }
        return result;
    }

    @Override
    public String getMethod() {
        return getRequest().getMethod().name();
    }

    @Override
    public Protocol getProtocol() {
        return AdapterRestletUtil.parseToRestletProtocol(getRequest().getProtocolVersion());
    }

    @Override
    public void sendResponse(org.restlet.Response responseInput)
            throws IOException {
    }

}
