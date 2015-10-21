/**
 * 项目名称: titilink
 * 文件名称: RestletServerWrapper.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.camel.rest.common.AdapterRestletUtil;
import com.titilink.camel.rest.common.RestletServerCall;
import com.titilink.common.log.AppLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.adapter.ServerCall;

import java.util.List;

/**
 * 将HttpCore 请求转化为Restlet请求
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class RestletServerWrapper {

    /**
     * 默认http端口
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * 默认https端口
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    /**
     * 非法端口值
     */
    private static final int INVALID_PORT = 0;

    private static final AppLogger LOGGER = AppLogger.getInstance(RestletServerWrapper.class);

    /**
     * The associated Restlet application.
     */
    private transient Application application;

    /**
     * The associated Restlet component.
     */
    private transient Component component;

    /**
     * The associated HTTP server helper.
     */
    private transient RestletServerHelper helper;

    public RestletServerWrapper(String host, int port, boolean supportSSL) {
        this.application = null;
        this.component = createComponent(supportSSL);
        this.helper = null;
        helper = createServer(host, port, supportSSL);
    }

    private Component getComponent() {
        return component;
    }

    protected Component createComponent(boolean supportSSL) {
        Component comp = new Component();
        comp.getStatusService().setEnabled(false);
        if (supportSSL) {
            comp.getClients().add(Protocol.HTTPS);
        } else {
            comp.getClients().add(Protocol.HTTP);
        }
        return comp;
    }

    public Application getApplication() {
        Application result = this.application;
        if (result == null) {
            synchronized (this) {
                result = this.application;
                if (result == null) {
                    result = createApplication(getComponent().getContext());
                    this.application = result;
                }
            }
        }
        return result;
    }

    private Application createApplication(Context parentContext) {
        Application app = AdapterRestletUtil.getApplication();

        // 没有配置，或加载不成功，使用默认的PluginableApplication
        if (app == null) {
            app = new PluginableApplication();
        }

        app.setContext(parentContext.createChildContext());
        return app;
    }

    public RestletServerHelper getServer() {
        return helper;
    }

    private RestletServerHelper createServer(String host, int port, boolean supportSSL) {
        LOGGER.debug("createServer() begin.");
        RestletServerHelper result = null;
        Component comp = getComponent();

        if (comp != null) {
            int serverPort = INVALID_PORT;
            if (port > INVALID_PORT) {
                serverPort = port;
            } else if (supportSSL) {
                serverPort = DEFAULT_HTTPS_PORT;
            } else {
                serverPort = DEFAULT_HTTP_PORT;
            }
            LOGGER.debug("host = " + host + " (if host is null, means use localhost) port = " + serverPort);
            Server server =
                    new Server(comp.getContext().createChildContext(), (List<Protocol>) null, host,
                            serverPort, comp);
            result = new RestletServerHelper(server);

            comp.getDefaultHost().attachDefault(getApplication());
        }

        LOGGER.debug("createServer() end.");
        return result;
    }

    public void init() {
        if ((getComponent() != null) && (getComponent().isStopped())) {
            try {
                getComponent().start();
            } catch (Exception e) {
                LOGGER.error("Exception while start component:", e);
            }
        }
    }

    public void service(ChannelHandlerContext ctx, FullHttpRequest request) {
        RestletServerHelper helperLocal = getServer();
        helperLocal.handle(createCall(ctx, helperLocal.getHelped(), request, null));
    }

    public void destroy() {
        if ((getComponent() != null) && (getComponent().isStarted())) {
            try {
                getComponent().stop();
            } catch (Exception e) {
                LOGGER.error("Exception while stop component:", e);
            }
        }
    }

    protected ServerCall createCall(ChannelHandlerContext ctx, Server server,
                                    HttpRequest request, HttpResponse response) {
        return new RestletServerCall(ctx, server, request, response);
    }

}
