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

import com.titilink.common.log.AppLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理RestletServerWrapper
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
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
