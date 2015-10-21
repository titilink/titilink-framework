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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     日志记载工具类，通过log4j记载项目日志，用法如下：
 *      AppLogger LOGGER = AppLogger.getInstance(T.class);
 * </pre>
 * <p>
 * @author by kam
 * @date 2015/04/29
 * @since v1.0.0
 */
public final class AppLogger {

    /**
     * AppLogger的缓冲实例
     */
    private static final Map<String, AppLogger> LOGGER_CACHE = new HashMap<String, AppLogger>();

    /**
     *
     */
    private static final String FQCN = AppLogger.class.getName();

    /**
     * log4j日志记载器
     */
    private static Logger logger;

    /**
     * @param configFile 配置文件
     * @param name       pattern
     */
    public AppLogger(File configFile, String name) {
        LogConfigReader.init(configFile);
        logger = Logger.getLogger(name);
    }

    private AppLogger(String className) {
        if (!LogConfigReader.isIsInit()) {
            LogConfigReader.init();
        }
        logger = Logger.getLogger(className);
    }

    private AppLogger(Class<?> clazz) {
        if (!LogConfigReader.isIsInit()) {
            LogConfigReader.init();
        }
        logger = Logger.getLogger(clazz);
    }

    public static AppLogger getInstance(Class<?> clazz) {
        String key = clazz.getName();
        if (!LOGGER_CACHE.containsKey(key)) {
            synchronized (LOGGER_CACHE) {
                if (!LOGGER_CACHE.containsKey(key)) {
                    AppLogger ap = new AppLogger(clazz);
                    LOGGER_CACHE.put(key, ap);
                }
            }
        }
        return LOGGER_CACHE.get(key);
    }

    public static void debug(Object message) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.DEBUG, message, null);
        }
    }

    public static void debug(String paramString, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.DEBUG, paramString, t);
        }
    }

    public static void debug(String paramString, Object paramObject) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.format(paramString, paramObject);
            logger.log(FQCN, Level.DEBUG, msgStr, null);
        }
    }

    public static void debug(String paramString, Object... paramArr) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.arrayFormat(paramString, paramArr);
            logger.log(FQCN, Level.DEBUG, msgStr, null);
        }
    }

    public static void info(Object message) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.INFO, message, null);
        }
    }

    public static void info(String paramString, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.INFO, paramString, t);
        }
    }

    public static void info(String paramString, Object paramObject) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.format(paramString, paramObject);
            logger.log(FQCN, Level.INFO, msgStr, null);
        }
    }

    public static void info(String paramString, Object... paramArr) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.arrayFormat(paramString, paramArr);
            logger.log(FQCN, Level.INFO, msgStr, null);
        }
    }

    public static void warn(Object message) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.WARN, message, null);
        }
    }

    public static void warn(String paramString, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.WARN, paramString, t);
        }
    }

    public static void warn(String paramString, Object paramObject) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.format(paramString, paramObject);
            logger.log(FQCN, Level.WARN, msgStr, null);
        }
    }

    public static void warn(String paramString, Object... paramArr) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.arrayFormat(paramString, paramArr);
            logger.log(FQCN, Level.WARN, msgStr, null);
        }
    }

    public static void error(Object message) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.ERROR, message, null);
        }
    }

    public static void error(String paramString, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.log(FQCN, Level.ERROR, paramString, t);
        }
    }

    public static void error(String paramString, Object paramObject) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.format(paramString, paramObject);
            logger.log(FQCN, Level.ERROR, msgStr, null);
        }
    }

    public static void error(String paramString, Object... paramArr) {
        if (logger.isDebugEnabled()) {
            String msgStr = LogFormatter.arrayFormat(paramString, paramArr);
            logger.log(FQCN, Level.ERROR, msgStr, null);
        }
    }

}
