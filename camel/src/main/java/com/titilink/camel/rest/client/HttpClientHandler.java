/**
 * 项目名称: titilink
 * 文件名称: HttpClientHandler.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.client;

import com.titilink.camel.rest.common.RestResponse;
import com.titilink.common.app.AppProperties;
import com.titilink.common.log.AppLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.restlet.data.Status;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 用于处理channel中的数据，判断是否报文过大，如果超过限制则返回一个异常消息体<br>
 * 如果没有超过限制，则从bytebuf中接收所有二进制数据并转化为字符串
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final AppLogger LOGGER = AppLogger.getInstance(HttpClientHandler.class);

    /**
     * 从多线程共享队列中获取RestResponse时出现超时的异常码
     */
    public static final int HTTP_CLINT_READ_TIMEOUT = 10001;

    /**
     * 响应报文体包过大异常码
     */
    public static final int HTTP_DOWN_PACKAGE_SIZE_OVER = 10002;

    /**
     * 从多线程共享队列中获取RestResponse的超时时间
     */
    private static final int HTTP_READ_TIME = 300000;

    /**
     * HTTP响应报文体最大的包的大小默认值1024*1024
     */
    private static final int DOWN_MAX_PACKET_SIZE = 1024 * 1024;

    /**
     * console需要处理的最大响应报文体包大小 app.pt.http.down.pakcet.size
     *
     * @since V1.0.5
     */
    private final static String APP_PT_HTTP_DOWN_PAKACET_SIZE = "app.pt.http.down.pakcet.size";

    /**
     * 多线程共享队列，用于放置netty一次http请求的响应对象
     */
    private final BlockingQueue<RestResponse> queue;

    private RestResponse result = null;

    private StringBuilder contentBufBuilder = new StringBuilder();

    /**
     * 首次从channel中获取响应报文数据
     */
    private volatile boolean firstHeader = true;

    /**
     * 当前channel中的netty http请求响应报文体是否过大
     */
    private volatile boolean isOverSizePackage = false;

    /**
     * 配置文件中配置的http 响应报文体的限制大小值
     */
    private final int maxPacketSize;

    public HttpClientHandler() {
        super();
        maxPacketSize = AppProperties.getAsInt(APP_PT_HTTP_DOWN_PAKACET_SIZE, DOWN_MAX_PACKET_SIZE);
        queue = new LinkedBlockingQueue<RestResponse>();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //是否读完了报文体
        boolean isReadOverMsg = false;
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            if (HttpHeaders.isContentLengthSet(response)) {
                int pkLength = (int) HttpHeaders.getContentLength(response);
                if (pkLength > maxPacketSize) {
                    LOGGER.error("http response packet size exceeds the system limit threshold, realSize={},maxPacketSize={}.", new Object[]{pkLength, maxPacketSize});
                    isOverSizePackage = true;
                } else {
                    isOverSizePackage = false;
                }
            }
            LOGGER.debug("status: {} , version: {}", response.getStatus(), response.getProtocolVersion());
            if (firstHeader) {
                // 实际的包处理大小，超过我们规定的大小，返回一个错误500的响应消息体
                Status status = isOverSizePackage ? Status.SERVER_ERROR_INTERNAL : Status.valueOf(((HttpResponse) msg).getStatus().code());
                result = new RestResponse(status);
                if (isOverSizePackage) {
                    //设置默认值，如果报文包大小未超过限制，那么该值将会被覆盖
                    result.setText("{\"code\":\"" + HTTP_DOWN_PACKAGE_SIZE_OVER + "\",\"message\":\"http response packet size exceeds the system limit threshold\"}");
                }
                firstHeader = false;
            }
            if (!response.headers().isEmpty()) {
                for (String name : response.headers().names()) {
                    for (String value : response.headers().getAll(name)) {
                        result.addHeader(name, value);
                    }
                }
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent)msg;
            //如果不是超过限制，那么才允许放到contentBufBuilder中，以防止占用过多内存
            if (!isOverSizePackage) {
                String sContent = content.content().toString(CharsetUtil.UTF_8);
                contentBufBuilder.append(sContent);
                if (content instanceof LastHttpContent) {
                    if (null != result && HttpResponseStatus.NO_CONTENT.code() != result.getStatus().getCode()) {
                        result.setText(contentBufBuilder.toString());
                    }
                    isReadOverMsg = true;
                }
            }
        }
        /**
         * 当是包超过大小 或者 包已经接收完毕时，才将result放到共享队列中
         */
        if ((!isReadOverMsg && isOverSizePackage) || (isReadOverMsg && !isOverSizePackage)) {
            if (!queue.offer(result)) {
                LOGGER.error("channelRead0. add result to queue failed");
            }
            reset();
        }
    }

    /**
     * 读取调用结果</br>
     * 如果设定的超时小于等于0，将设置读超时为默认值10s
     *
     * @param msTimeout 设置读超时时间
     * @return
     */
    public RestResponse getResult(long msTimeout) {
        RestResponse result = null;
        // 如果设定的超时小于等于0，将设置读超时为默认值10s
        long readTimeout = msTimeout <=0 ? HTTP_READ_TIME : msTimeout;
        for (; ; ) {
            try {
                result = queue.poll(readTimeout, TimeUnit.MILLISECONDS);
                // 如果是null,则设置一个错误码
                if (null == result) {
                    LOGGER.error("getResult timeout.");
                    result = new RestResponse(new Status(HTTP_CLINT_READ_TIMEOUT));
                }
                return result;
            } catch (InterruptedException e) {
                LOGGER.error("get result from queue had been interrupted.");
            } finally {
                queue.clear();
            }
        }
    }

    public RestResponse getResult() {
        return getResult(HTTP_READ_TIME);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        reset();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught:", cause);
        queue.clear();
        reset();
        ctx.close();
    }

    /**
     * 把对象设置成未接收状态，用于一次调用的结束
     */
    private void reset() {
        // 设置读头信息标志，意思为第一次读头信息
        firstHeader = true;
        // 重置内容缓存
        contentBufBuilder.setLength(0);
    }

}
