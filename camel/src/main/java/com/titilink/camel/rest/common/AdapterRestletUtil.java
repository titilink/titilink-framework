/**
 * 项目名称: titilink
 * 文件名称: AdapterRestletUtil.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.common;

import com.titilink.common.log.AppLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

import java.io.*;
import java.util.*;

/**
 * <pre>
 * netty适配Restlet工具类
 *  配置文件查找方法：
 * 1、在当前应用的工作目录下（相对路径）config子目录下寻找并加载
 * 2、在当前应用的classpath的config子目录下寻找并加载
 * </pre>
 * <p>
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class AdapterRestletUtil {

    private static final AppLogger LOGGER = AppLogger.getInstance(AdapterRestletUtil.class);

    private static final String PLUGINABLE_APPLICATION = "com.huawei.camel.server.application.PluginableApplication";

    public static final int DEFAULT_HTTP_PORT = -1;

    public static final int DEFAULT_HTTPS_PORT = -1;

    private static final String GLOBLE_CONFIG = "config" + File.separatorChar + "rest.properties";

    private static final String GLOBLE_CONFIG_HTTP_PORT = "server.rest.http.port";

    private static final String GLOBLE_CONFIG_HTTPS_PORT = "server.rest.https.port";

    private static final String GLOBLE_CONFIG_URI_PATTERN = "server.rest.uri.pattern";

    private static final String GLOBLE_CONFIG_RESTLET_APPLICATION = "server.rest.application.class";

    private static final String GLOGLE_CONFIG_APPLICATION_PLUGINS = "server.rest.application.plugins";

    private static final String NETTY_CONTEXT = "netty.channel.http.context";

    private static final String COOKIE = "cookies";

    private static final String DATE = "date";

    public static final Properties PROP;

    static {
        PROP = new Properties();
        File f = new File(GLOBLE_CONFIG);
        if (f.exists()) {
            FileInputStream bis = null;
            try {
                bis = new FileInputStream(f);
                PROP.load(bis);
            } catch (FileNotFoundException ex) {
                LOGGER.error("read file failed! ");
            } catch (IOException ex) {
                LOGGER.error("read file failed! ");
            } catch (RuntimeException ex) {
                LOGGER.error("read file failed! ");
            } catch (Exception ex) {
                LOGGER.error("read file failed! ");
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("InputStream closed failed! ");
                }
                bis = null;
            }
        } else {
            InputStream is = null;
            try {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GLOBLE_CONFIG);
                PROP.load(is);
            } catch (IOException e) {
                LOGGER.error("read file failed! ");
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOGGER.error("InputStream closed failed! ");
                    }
                }
                is = null;
            }
        }

    }

    /**
     * 获取Restlet上下文
     * 将netty的上下文保存到Context中
     *
     * @param ctx netty的上下文
     * @return
     */
    public static Context parseToRestletContext(ChannelHandlerContext ctx) {
        if (null == ctx) {
            LOGGER.error("ctx=null");
            return null;
        }
        Context context = new Context();
        //Copy ctx到context中
        context.getAttributes().put(NETTY_CONTEXT, ctx);
        return context;
    }

    /**
     * 获取Restlet HttpRequest
     *
     * @param request netty的Httprequest
     * @return
     */
    public static void parseToRestletHttpRequest(io.netty.handler.codec.http.HttpRequest request,
                                                 HttpRequest restletRequest) {
        if (null == request || null == restletRequest) {
            LOGGER.error("invalid param, request or restletRequest is null");
            return;
        }
        //copy HttpRequest头部
        HttpHeaders headers = request.headers();
        for (Map.Entry<String, String> entry : headers.entries()) {
            HttpRequest.addHeader(restletRequest, entry.getKey(), entry.getValue());
        }
        //请求方法
        Method requestMethod = new Method(request.getMethod().name());
        restletRequest.setMethod(requestMethod);

        //协议
        Protocol protocol = new Protocol(request.getProtocolVersion().protocolName());
        restletRequest.setProtocol(protocol);

        //Cookie
        Set<Cookie> cookies = null;
        @SuppressWarnings("rawtypes")
        Series<org.restlet.data.Cookie> restletCookies = (Series) Collections.emptySet();
        String value = request.headers().get(COOKIE);
        if (null == value) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        for (Cookie cookie : cookies) {
            restletCookies.add(parseToRestletCookie(cookie));
        }
        restletRequest.setCookies(restletCookies);
    }

    public static void parseToNettyFullResponse(RestletServerCall httpCall,
                                                org.restlet.engine.adapter.HttpResponse response, io.netty.handler.codec.http.FullHttpResponse nettyResponse) {
        if (null == nettyResponse) {
            LOGGER.error("nettyResponse=null");
            return;
        }
        HttpHeaders nettyHeaders = nettyResponse.headers();

        if (null != response) {
            Series<Header> headers = response.getHeaders();
            if (null != headers) {
                for (Header parameter : headers) {
                    if (null == parameter) {
                        continue;
                    }
                    String value = parameter.getValue();
                    nettyHeaders.add(parameter.getName(), value);
                }
            }
        }
        nettyHeaders.set(DATE, new Date());

        // remove version information for security issue
        List<Map.Entry<String, String>> entries = nettyHeaders.entries();
        if (entries != null) {
            for (Map.Entry<String, String> entry : entries) {
                if (entry != null) {
                    String value = entry.getValue();
                    if (value != null
                            && (Engine.VERSION_HEADER.equalsIgnoreCase(value) || value.contains(Engine.VERSION_HEADER))) {
                        nettyHeaders.set(entry.getKey(), value.replaceAll(Engine.VERSION_HEADER, ""));
                    }
                }
            }
        }
    }

    /**
     * 获取Cookie
     *
     * @param cookie
     * @return
     */
    public static org.restlet.data.Cookie parseToRestletCookie(Cookie cookie) {
        if (null == cookie) {
            LOGGER.error("cookie=null");
            return null;
        }
        org.restlet.data.Cookie restletCookie = new org.restlet.data.Cookie();
        restletCookie.setDomain(cookie.getDomain());
        restletCookie.setVersion(cookie.getVersion());
        restletCookie.setName(cookie.getName());
        restletCookie.setValue(cookie.getValue());
        restletCookie.setPath(cookie.getPath());
        return restletCookie;
    }

    protected static Protocol parseToRestletProtocol(HttpVersion protocol) {
        if (null == protocol) {
            LOGGER.error("protocol=null");
            return null;
        }
        final String schemeName = protocol.protocolName();
        Protocol restletProtocol =
                new Protocol(schemeName, schemeName.toUpperCase(), schemeName.toUpperCase() + " Protocol", "",
                        Protocol.UNKNOWN_PORT, false, getProtocolVersion(protocol));
        return restletProtocol;
    }

    protected static String getProtocolVersion(HttpVersion protocol) {
        String result = null;
        final String protocolVersion = protocol.toString();
        final int index = protocolVersion.indexOf('/');

        if (index != -1) {
            result = protocolVersion.substring(index + 1);
        }
        return result;
    }

    /**
     * 获取application
     *
     * @return
     */
    public static Application getApplication() {
        String appClazz = PROP.getProperty(GLOBLE_CONFIG_RESTLET_APPLICATION);

        // 没有配置，则使用默认的application
        if (appClazz == null) {
            LOGGER.info("application class:{} not configured, use default:{}", new Object[]{
                    GLOBLE_CONFIG_RESTLET_APPLICATION, PLUGINABLE_APPLICATION});
            appClazz = PLUGINABLE_APPLICATION;
        }

        return newInstance(appClazz);
    }

    /**
     * 读取plugins配置并new出实例
     *
     * @return
     */
    public static ApplicationPlugin[] getPlugins() {
        String appPlugins = PROP.getProperty(GLOGLE_CONFIG_APPLICATION_PLUGINS);
        if (appPlugins == null || appPlugins.trim().isEmpty()) {
            LOGGER.error("No plugins provided:{}", GLOGLE_CONFIG_APPLICATION_PLUGINS);
            return null;
        }

        String[] clazzes = appPlugins.trim().split(";");

        LOGGER.debug("plugins={}", appPlugins);

        List<ApplicationPlugin> pluginList = new ArrayList<ApplicationPlugin>();
        for (String clazz : clazzes) {
            if (clazz != null && !clazz.trim().isEmpty()) {
                ApplicationPlugin plugin = newInstance(clazz.trim());
                if (plugin != null) {
                    pluginList.add(plugin);
                }
            }
        }

        ApplicationPlugin[] plugins = pluginList.toArray(new ApplicationPlugin[pluginList.size()]);
        return plugins;
    }

    public static int getHttpPort() {
        //设置侦听端口
        String sHttpPort = PROP.getProperty(GLOBLE_CONFIG_HTTP_PORT);
        return getPort(sHttpPort);
    }

    public static int getHttpsPort() {
        //设置侦听端口
        String sHttpsPort = PROP.getProperty(GLOBLE_CONFIG_HTTPS_PORT);
        return getPort(sHttpsPort);
    }

    /**
     * 读取rest.properties的对应配置信息
     *
     * @param key
     * @param defaultValue
     * @return value
     */
    public static String getProperty(String key, String defaultValue) {
        String propValue = PROP.getProperty(key, defaultValue);
        return (null == propValue || "".equals(propValue)) ? defaultValue : propValue;
    }

    /**
     * 读取rest.properties的对应配置信息
     *
     * @param key
     * @return value
     */
    public static String getProperty(String key) {
        LOGGER.debug("getProperty={}", PROP.getProperty(key));
        return PROP.getProperty(key);
    }

    /**
     * 读取rest.properties的对应配置信息
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            String sValue = getProperty(key, null);
            if (null == sValue) {
                return defaultValue;
            } else {
                return Integer.valueOf(sValue);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("rest.properties getIntProperty format error, please check the config.", e);
            return defaultValue;
        }
    }

    private static int getPort(String sPort) {
        if (sPort == null || sPort.isEmpty()) {
            return DEFAULT_HTTP_PORT;
        }

        try {
            final int port = Integer.parseInt(sPort);
            return port;
        } catch (NumberFormatException e) {
            LOGGER.error("Exception while getHttpPort:{}", e.toString());
            return DEFAULT_HTTP_PORT;
        }
    }

    public static String getUriPattern() {
        return PROP.getProperty(GLOBLE_CONFIG_URI_PATTERN);
    }

    /**
     * @param klass
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T newInstance(String klass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) (Class.forName(klass, true, loader));
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException throw while Class.forName.");
            return null;
        }

        if (null == clazz) {
            LOGGER.error("Class.forName returns a null class.");
            return null;
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("Exception while newInstance:");
            return null;
        } catch (IllegalAccessException e) {
            LOGGER.error("Exception while newInstance:");
            return null;
        }
    }

}
