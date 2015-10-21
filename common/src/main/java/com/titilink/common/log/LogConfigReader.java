/**
 * 项目名称: titilink
 * 文件名称: LogConfigReader.java
 * Date: 2015/4/29
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
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
 * author by ganting
 * date 2015-04-29
 * since v1.0.0
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
