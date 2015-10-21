/**
 * 项目名称: titilink
 * 文件名称: RestletServerWrapperMgt.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.common.log.AppLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理RestletServerWrapper
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public final class RestletServerWrapperMgt {

    /**
     * 日志打印
     */
    private static final AppLogger LOGGER = AppLogger.getInstance(RestletServerWrapperMgt.class);

    /**
     * 默认httpwrapper的key
     */
    public static final String DEFAULT_HTTP = "http";

    /**
     * 默认httpswrapper的key
     */
    public static final String DEFAULT_HTTPS = "https";

    /**
     * 默认wrapper映射
     */
    private static Map<String, RestletServerWrapper> defaultWrapperMap =
            new HashMap<String, RestletServerWrapper>();

    /**
     * 获取默认httpwrapper
     *
     * @return httpwrapper实例
     * @throws Exception
     */
    public static RestletServerWrapper getDefaultHttpWrapper()
            throws Exception {
        return getDefaultWrapper(DEFAULT_HTTP);
    }

    /**
     * 获取默认httpwrapper
     *
     * @return httpwrapper实例
     * @throws Exception
     */
    public static RestletServerWrapper getDefaultHttpsWrapper()
            throws Exception {
        return getDefaultWrapper(DEFAULT_HTTPS);
    }

    /**
     * 获取默认wrapper
     * http和https各一个
     * 如果支持SSL返回https的wrapper，否则为http的wrapper
     *
     * @return httpwrapper实例
     * @throws Exception
     */
    public static RestletServerWrapper getDefaultWrapper(boolean supportSSL)
            throws Exception {
        if (supportSSL) {
            return getDefaultHttpsWrapper();
        } else {
            return getDefaultHttpWrapper();
        }
    }

    /**
     * 获取默认wrapper
     * http和https各一个
     *
     * @return wrapper实例
     * @throws Exception
     */
    private static RestletServerWrapper getDefaultWrapper(String defaultPotocol)
            throws Exception {
        return defaultWrapperMap.get(defaultPotocol);
    }

    /**
     * 清除所有缓存的restletServerWrappers
     */
    public static void cleanAll() {
        for (RestletServerWrapper wrapper : defaultWrapperMap.values()) {
            if (null != wrapper) {
                wrapper.destroy();
            }
        }
        defaultWrapperMap.clear();
    }

    /**
     * 缓存默认使用的restletserverwrapper
     * http和https各一个
     *
     * @param wrapper
     * @param supportSSL
     */
    public static void cacheDefaultWrapper(RestletServerWrapper wrapper, boolean supportSSL) {
        LOGGER.debug("cacheDefaultWrapper wrapper={}, supportSSL={}", new Object[]{wrapper, supportSSL});
        if (supportSSL) {
            defaultWrapperMap.put(DEFAULT_HTTPS, wrapper);
        } else {
            defaultWrapperMap.put(DEFAULT_HTTP, wrapper);
        }
    }

}
