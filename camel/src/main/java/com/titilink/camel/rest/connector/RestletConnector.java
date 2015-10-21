/**
 * Copyright 2005-2015 titilink
 * <p/>
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * <p/>
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * <p/>
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * <p/>
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * <p/>
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * <p/>
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * <p/>
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * <p/>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 * <p/>
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.camel.rest.connector;

import com.titilink.camel.rest.common.AdapterRestletUtil;
import com.titilink.camel.rest.common.RestletServerCall;
import com.titilink.camel.rest.server.PluginableApplication;
import com.titilink.camel.rest.server.RestletServerHelper;
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
 * Restlet Http Adapter适配器
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public class RestletConnector implements HttpConnector {

    private static final AppLogger LOGGER = AppLogger.getInstance(RestletConnector.class);

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

    /**
     * 构造方法
     *
     * @param host
     * @param port
     * @param supportSSL
     */
    public RestletConnector(String host, int port, boolean supportSSL) {
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
