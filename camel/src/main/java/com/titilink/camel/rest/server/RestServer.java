/**
 * 项目名称: titilink
 * 文件名称: RestServer.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.common.log.AppLogger;

/**
 * Rest服务端
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public final class RestServer {

    private static final AppLogger LOGGER = AppLogger.getInstance(RestServer.class);

    /**
     * 启动Rest服务端
     */
    public static void startup() {
        LOGGER.debug("RestServer startup...");
        CamelServer.startup();
        LOGGER.debug("RestServer startup success...");
    }

    /**
     * 停止Rest服务端
     */
    public static void stop() {
        LOGGER.debug("RestServer stop...");
        CamelServer.shutdown();
        LOGGER.debug("RestServer stop success...");
    }

}
