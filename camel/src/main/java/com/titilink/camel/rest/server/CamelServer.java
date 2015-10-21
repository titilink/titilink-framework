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
import com.titilink.common.log.AppLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 具体服务端
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class CamelServer {

    private static final String LOCAL_HOST = "127.0.0.1";

    private static final String SERVER_REST_IP = "server.rest.ip";

    /**
     * 默认SO_BACKLOG
     */
    private static final int DEFAULT_SO_BACKLOG = 100;

    private static final int DEFAULT_BOSS_NIO_EVENT_NUM = 10;

    /**
     * 默认netty处理线程数
     */
    private static final int DEFAULT_NIO_EVENT_NUM = 200;

    private static final AppLogger LOGGER = AppLogger.getInstance(CamelServer.class);

    private static final List<EventLoopGroup> BOSS_GROUP_LIST = new ArrayList<EventLoopGroup>();

    private static final List<EventLoopGroup> WORKER_GROUP_LIST = new ArrayList<EventLoopGroup>();

    private static final List<Channel> CHANNEL_LIST = new ArrayList<Channel>();

    /**
     * 启动REST服务端
     */
    public static void startup() {
        LOGGER.debug("CamelServer.startup() begin");
        if (!BOSS_GROUP_LIST.isEmpty() || !WORKER_GROUP_LIST.isEmpty() || !CHANNEL_LIST.isEmpty()) {
            LOGGER.error("Stop the server first.");
            return;
        }

        //同时侦听HTTP和HTTPS
        startHttpMonitor();
        startHttpsMonitor();
    }

    public static void shutdown() {
        for (Channel ch : CHANNEL_LIST) {
            if (ch != null) {
                ChannelFuture cf = ch.close();
                cf.awaitUninterruptibly();
                ch = null;
            }
        }

        for (EventLoopGroup group : BOSS_GROUP_LIST) {
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }
        }

        for (EventLoopGroup group : WORKER_GROUP_LIST) {
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }
        }

        CHANNEL_LIST.clear();
        BOSS_GROUP_LIST.clear();
        WORKER_GROUP_LIST.clear();
        RestletServerWrapperMgt.cleanAll();
    }

    private static void run(int port, boolean supportSSL) {
        LOGGER.info("CamelServer run({}, {})", port, supportSSL);

        EventLoopGroup bossGroup = new NioEventLoopGroup(DEFAULT_BOSS_NIO_EVENT_NUM);
        BOSS_GROUP_LIST.add(bossGroup);
        EventLoopGroup workerGroup = new NioEventLoopGroup(DEFAULT_NIO_EVENT_NUM);
        WORKER_GROUP_LIST.add(workerGroup);

        // 在启动时候把wrapper拉起，并缓存起来，其中host为null代表使用本地ip
        String host = AdapterRestletUtil.getProperty(SERVER_REST_IP, LOCAL_HOST);
        RestletServerWrapper restletServerWrapper = new RestletServerWrapper(host, port, supportSSL);
        RestletServerWrapperMgt.cacheDefaultWrapper(restletServerWrapper, supportSSL);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer(supportSSL));
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.SO_BACKLOG, DEFAULT_SO_BACKLOG);

            Channel ch = b.bind(new InetSocketAddress(host, port)).sync().channel();
            CHANNEL_LIST.add(ch);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException while start server.");
        } catch (Throwable t) {
            LOGGER.error("CamelServer run throws error.");
        } finally {
            LOGGER.info("CamelServer run() : shutdown");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static void startHttpsMonitor() {
        LOGGER.debug("CamelServer.startHttpsMonitor() begin");
        Thread worker = new Thread() {
            public void run() {
                LOGGER.debug("Camel-https-worker run");
                final int httpsPort = AdapterRestletUtil.getHttpsPort();
                if (httpsPort == -1) {
                    LOGGER.debug("Camel-https-worker abort.");
                    return;
                }
                CamelServer.run(httpsPort, true);
            }
        };
        worker.setName("Camel-https-worker-" + AdapterRestletUtil.getHttpsPort());
        worker.start();
        LOGGER.debug("CamelServer.startHttpsMonitor() end.");
    }

    private static void startHttpMonitor() {
        LOGGER.debug("CamelServer.startHttpMonitor() begin");
        Thread worker = new Thread() {
            public void run() {
                LOGGER.debug("Camel-http-worker run");
                final int httpPort = AdapterRestletUtil.getHttpPort();
                if (httpPort == -1) {
                    LOGGER.debug("Camel-http-worker abort.");
                    return;
                }
                CamelServer.run(httpPort, false);
            }
        };
        worker.setName("Camel-http-worker-" + AdapterRestletUtil.getHttpPort());
        worker.start();
        LOGGER.debug("CamelServer.startHttpMonitor() end.");
    }

}
