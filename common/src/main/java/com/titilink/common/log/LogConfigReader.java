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
package com.titilink.common.log;

import com.titilink.common.app.AppContext;
import com.titilink.common.app.AppProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * log4j文件读取类
 * <p>
 * @author by ganting
 * @date 2015/04/29
 * @since v1.0.0
 */
public final class LogConfigReader {

    private static volatile boolean isInit = false;

    private static final int LOG_RELOAD_DELAY = 60000;

    private static String lastpath = "";

    private static final ReentrantLock LOCKER = new ReentrantLock();

    private static long delay = AppProperties.getAsInt("LogConfReloadDelay", LOG_RELOAD_DELAY);

    public static boolean isIsInit() {
        return isInit;
    }

    public static void reload(Properties prop) {
        PropertyConfigurator.configure(prop);
    }

    public static void reload() {
        PropertyConfigurator.configure(lastpath);
    }

    public static void init(File configFile) {
        try {
            LOCKER.lock();
            PropertyConfigurator.configure(configFile.getPath());
        } finally {
            LOCKER.unlock();
        }
    }

    public static synchronized void init() {
        if (!isInit) {
            lastpath = AppContext.getInstance().getAppHome() + "log4j.properties";
            Enumeration<?> e = LogManager.getCurrentLoggers();
            if (!e.hasMoreElements()) {
                loadConf();
            } else {
                int i = AppProperties.getAsInt("resource_LOG4J_REUSE", 0);
                if (i == 0) {
                    loadConf();
                }
            }
            isInit = true;
        }
    }

    private static void loadConf() {
        PropertyConfigurator.configureAndWatch(lastpath, delay);
    }

}
