/**
 * 项目名称: titilink
 * 文件名称: SilvanMain.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.silvan.main;

import com.titilink.camel.rest.server.RestServer;
import com.titilink.common.log.AppLogger;

/**
 * silvan启动进程入口函数
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public final class SilvanMain {

    private static final AppLogger LOGGER = AppLogger.getInstance(SilvanMain.class);

    /**
     * 启动silvan
     *
     * @param args
     */
    public static void main(String[] args) {
        SilvanMain silvanMain = new SilvanMain();
        silvanMain.start();
    }

    private void start() {
        LOGGER.debug("start silvan...");
        RestServer.startup();
        LOGGER.debug("start silvan success...");
    }

}
