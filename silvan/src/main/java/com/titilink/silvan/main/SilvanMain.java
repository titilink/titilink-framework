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
package com.titilink.silvan.main;

import com.titilink.camel.rest.server.RestServer;
import com.titilink.common.log.AppLogger;

/**
 * silvan启动进程入口函数
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
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
