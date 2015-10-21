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

import com.titilink.camel.rest.common.RestResponse;
import com.titilink.common.app.RestletClientProperties;
import com.titilink.common.log.AppLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.restlet.data.ChallengeResponse;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 具体服务客户端
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class CamelClient {

    private static final String REST_CLIENT_CONN_COUNT = "client.rest.connCount";

    private static final String KEY_SPLIT_MARK = "_";

    private static final int DEFAULT_AT_LEAST_CONNECTION_NUM = 5;

    private static int minConnectionNum = DEFAULT_AT_LEAST_CONNECTION_NUM;

    private static final int HTTP_READ_TIME = 300000;

    /**
     * 缓存client
     * key为host + port + isSSL
     * value为client对象
     */
    private static final Map<String, CamelClient> CLIENTS = new HashMap<String, CamelClient>();

    /**
     * 需要忽略的入参头信息
     */
    private static final List<String> HEADER_NEED_SKIP = new ArrayList<String>();

    static {
        HEADER_NEED_SKIP.add("connection");
        HEADER_NEED_SKIP.add("content-length");
        HEADER_NEED_SKIP.add("host");
        HEADER_NEED_SKIP.add("user-agent");
        HEADER_NEED_SKIP.add("transfer-encoding");
        HEADER_NEED_SKIP.add("x-real-ip");
        minConnectionNum = RestletClientProperties.getIntProperty("camelClientMinConnectionNum",
                DEFAULT_AT_LEAST_CONNECTION_NUM);
    }

    /**
     * 日志打印组件
     */
    private static final AppLogger LOGGER = AppLogger.getInstance(CamelClient.class);

    private EventLoopGroup workerGroup;

    /**
     * host为发送目标的ip
     */
    private final String host;

    /**
     * port为发送目标的port
     */
    private final int port;

    /**
     * 连接数
     */
    private final int connAmout;

    private final Bootstrap bootstrap;

    /**
     * 通道队列，用于缓存通道，每个通道既一条连接
     */
    private final BlockingQueue<Channel> channelQueue;

    /**
     * 初始化标志位
     */
    private AtomicBoolean initFlag = new AtomicBoolean(false);

    /**
     * 默认每个client对象的连接数
     */
    private static final int DEFAULT_CONN_AMOUNTS = 30;

    /**
     * <默认构造函数>
     * 禁止外部初始化
     */
    private CamelClient(String host, int port, int connAmout, boolean ssl) {
        this.host = host;
        this.port = port;

        //Configure the client.
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new HttpClientInitializer(ssl));
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.remoteAddress(this.host, this.port);

        //at least one connection
        this.connAmout = (connAmout > 0) ? connAmout : minConnectionNum;
        channelQueue = new LinkedBlockingQueue<Channel>();
    }

    private Channel connect() {
        return bootstrap.connect().syncUninterruptibly().channel();
    }

    private static String genClientKey(String host, int port, boolean ssl) {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(host);
        sbuilder.append(KEY_SPLIT_MARK);
        sbuilder.append(port);
        sbuilder.append(KEY_SPLIT_MARK);
        sbuilder.append(ssl);
        return sbuilder.toString();
    }

    private void initConns() {
        if (!initFlag.compareAndSet(false, true)) {
            return;
        }

        LOGGER.info("initConns connAmout is {}", minConnectionNum);
        for (int i = 0; i < minConnectionNum; i++) {
            if (!channelQueue.offer(connect())) {
                LOGGER.error("initConns. channel dose not be added to the queue");
            }
        }
    }

    private static boolean isSSl(String scheme) {
        return (null != scheme && "https".equalsIgnoreCase(scheme)) ? true : false;
    }

    private static CamelClient getInstance(URI uri) {
        boolean ssl = isSSl(uri.getScheme());
        String clientKey = genClientKey(uri.getHost(), uri.getPort(), ssl);
        CamelClient client = CLIENTS.get(clientKey);

        if (null != client) {
            return client;
        } else {
            synchronized (CamelClient.class) {
                client = CLIENTS.get(clientKey);
                if (null == client) {
                    client = new CamelClient(uri.getHost(), uri.getPort(), getClientConnCount(), ssl);
                    CLIENTS.put(clientKey, client);
                }
            }
            return client;
        }
    }

    private static int getClientConnCount() {
        return RestletClientProperties.getIntProperty(REST_CLIENT_CONN_COUNT, DEFAULT_CONN_AMOUNTS);
    }

    private BlockingQueue<Channel> getChannelQueue() {
        return channelQueue;
    }

    /**
     * 发送http get请求
     *
     * @param uri               请求路径
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @return
     */
    public static RestResponse handleGet(URI uri, Map<String, String> additionalHeaders,
                                         ChallengeResponse challengeResponse) {
        return handleGet(uri, additionalHeaders, challengeResponse, HTTP_READ_TIME);
    }

    /**
     * 发送Http post请求
     *
     * @param uri               请求路径
     * @param reqBuffer         数据
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @return
     */
    public static RestResponse handlePost(URI uri, byte[] reqBuffer, Map<String, String> additionalHeaders,
                                          ChallengeResponse challengeResponse) {
        return handlePost(uri, reqBuffer, additionalHeaders, challengeResponse, HTTP_READ_TIME);
    }

    /**
     * put方法
     *
     * @param uri               请求路径
     * @param reqBuffer         数据
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @return json 结果
     */
    public static RestResponse handlePut(URI uri, byte[] reqBuffer, Map<String, String> additionalHeaders,
                                         ChallengeResponse challengeResponse) {
        return handlePut(uri, reqBuffer, additionalHeaders, challengeResponse, HTTP_READ_TIME);
    }

    /**
     * delete方法
     *
     * @param uri               请求路径
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @return
     */
    public static RestResponse handleDelete(URI uri, Map<String, String> additionalHeaders,
                                            ChallengeResponse challengeResponse) {
        return handleDelete(uri, additionalHeaders, challengeResponse, HTTP_READ_TIME);
    }

    /**
     * 发送http get请求
     *
     * @param uri               请求路径
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @param msTimeout         读超时设置，如果小于等于0，将会设置成默认值10s
     * @return
     */
    public static RestResponse handleGet(URI uri, Map<String, String> additionalHeaders,
                                         ChallengeResponse challengeResponse, long msTimeout) {
        return handle(HttpMethod.GET, uri, null, additionalHeaders, challengeResponse, true, msTimeout);
    }

    /**
     * 发送Http post请求
     *
     * @param uri               请求路径
     * @param reqBuffer         数据
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @param msTimeout         读超时设置，如果小于等于0，将会设置成默认值10s
     * @return
     */
    public static RestResponse handlePost(URI uri, byte[] reqBuffer, Map<String, String> additionalHeaders,
                                          ChallengeResponse challengeResponse, long msTimeout) {
        return handle(HttpMethod.POST, uri, reqBuffer, additionalHeaders, challengeResponse, true, msTimeout);
    }

    /**
     * put方法
     *
     * @param uri               请求路径
     * @param reqBuffer         数据
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param msTimeout         读超时设置，如果小于等于0，将会设置成默认值10s
     * @return json 结果
     */
    public static RestResponse handlePut(URI uri, byte[] reqBuffer, Map<String, String> additionalHeaders,
                                         ChallengeResponse challengeResponse, long msTimeout) {
        return handle(HttpMethod.PUT, uri, reqBuffer, additionalHeaders, challengeResponse, true, msTimeout);
    }

    /**
     * delete方法
     *
     * @param uri               请求路径
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse
     * @param msTimeout         读超时设置，如果小于等于0，将会设置成默认值10s
     * @return
     */
    public static RestResponse handleDelete(URI uri, Map<String, String> additionalHeaders,
                                            ChallengeResponse challengeResponse, long msTimeout) {
        return handle(HttpMethod.DELETE, uri, null, additionalHeaders, challengeResponse, true, msTimeout);
    }

    /**
     * 添加默认的方法，支持非GET POST PUT DELETE请求
     *
     * @param uri               请求路径
     * @param reqBuffer         数据
     * @param additionalHeaders 自定义header，如鉴权用户名密码token 等
     * @param challengeResponse challengeResponse
     * @param msTimeout         超时时间
     * @return RestResponse
     * @since v1.0.3
     */
    public static RestResponse handleDefault(String method, URI uri, byte[] reqBuffer, Map<String, String> additionalHeaders,
                                             ChallengeResponse challengeResponse, long msTimeout) {
        return handle(HttpMethod.valueOf(method.toUpperCase()), uri, reqBuffer, additionalHeaders, challengeResponse, true, msTimeout);
    }

    private static RestResponse handle(HttpMethod httpMethod, URI uri, byte[] reqBuffer,
                                       Map<String, String> additionalHeaders, ChallengeResponse challengeResponse, boolean longConn, long msTimeout) {
        LOGGER.debug("handleRequest begin. uri={}, method={}, longConn={}, msTimeout={}", uri, httpMethod, longConn, msTimeout);
        if (null == uri) {
            LOGGER.error("uri is null.");
            return null;
        }

        CamelClient client = getInstance(uri);

        // Create a simple GET request with just headers.
        FullHttpRequest request = null;
        String requestUri = uri.getRawQuery() == null ? uri.getRawPath() : uri.getRawPath() + "?" + uri.getRawQuery();
        if (null == reqBuffer) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, requestUri);
        } else {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(reqBuffer);
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, requestUri, byteBuf);
            request.headers().add(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        }
        request.headers().add(HttpHeaders.Names.HOST, uri.getHost() + ':' + uri.getPort());
        request.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().add("user-agent", "HttpClient");
        if (null != additionalHeaders) {
            Iterator<Map.Entry<String, String>> iter = additionalHeaders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                String key = entry.getKey();
                String headerValue = entry.getValue();
                if (null == key || HEADER_NEED_SKIP.contains(key.toLowerCase())) {
                    continue;
                }
                if (null != headerValue) {
                    request.headers().add(key, headerValue);
                }
            }
        }

        // duel with challengeResponse
        if (null != challengeResponse) {
            Series<Header> headers = new Series<Header>(Header.class);
            for (Map.Entry<String, String> entry : request.headers().entries()) {
                if (null != entry && null != entry.getValue()) {
                    headers.add(entry.getKey(), entry.getValue());
                }
            }
            String authHeader = org.restlet.engine.security.AuthenticatorUtils.formatResponse(challengeResponse, null, headers);
            for (Header header : headers) {
                if (null != header) {
                    request.headers().add(header.getName(), header.getValue());
                }
            }
            if (authHeader != null) {
                request.headers().add(HeaderConstants.HEADER_AUTHORIZATION, authHeader);
            }
        }
        RestResponse result = null;
        int retry = 3;
        boolean success = false;
        while (!success && retry > 0) {
            Channel channel = null;

            // 如果为长连接，需要重队列中获取通道，否则每次生成新的通道
            if (longConn) {
                channel = getLongChannel(client);
            } else {
                LOGGER.info("open a new channel");
                channel = client.connect();
            }
            // 判断channel是否为空
            if (null == channel || null == channel.pipeline()) {
                LOGGER.error("channle is null or channel.pipeline is null.");
                return null;
            }
            final HttpClientHandler httpClientHandler = (HttpClientHandler) channel.pipeline().get("handler");

            if (null == httpClientHandler) {
                LOGGER.error("httpClientHandler is null.");
                return null;
            }

            try {
                channel.writeAndFlush(request);
                result = httpClientHandler.getResult(msTimeout);
                success = true;
            } catch (Throwable t) {
                LOGGER.error("send request by channel raised a exception:", t);
                channel.close();
                if (t instanceof SSLException) {
                    success = false;
                } else {
                    //其他异常不重试
                    success = true;
                }

            } finally {
                retry--;
                // 长连接需要缓存通道到队列中
                // 当长连接时，判断是否队列大小小于设置的大小并且获取结果没有超时时才缓存该连接，避免下次重用该连接时上一次的连接的服务端还在往通道里写数据
                if (longConn && client.getChannelQueue().size() < client.connAmout && !isTimeOut(result)) {
                    try {
                        client.getChannelQueue().put(channel);
                    } catch (InterruptedException e) {
                        LOGGER.error("put channel to channelQueue had been interrupted.");
                    }
                } else {
                    LOGGER.info("Close channel.");
                    channel.close();
                }
            }
        }

        if (result == null) {
            LOGGER.error("response is null.");
        }
        return result;
    }

    private static boolean isTimeOut(RestResponse result) {
        return result != null && result.getStatus() != null && result.getStatus().getCode() == 10001;
    }

    private static Channel getLongChannel(CamelClient client) {
        client.initConns();
        LOGGER.info("channelQueue size:{}", client.getChannelQueue().size());
        Channel channel = null;
        try {
            channel = client.getChannelQueue().poll(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("get channel from queue had been interrupted.");
        }
        if (channel == null || !channel.isActive()) {
            if (channel != null) {
                channel.close();
            }
            //创建新的channel并返回
            channel = client.connect();

            LOGGER.warn("channel is close,open a new one in the queue");
        }
        return channel;
    }

}
