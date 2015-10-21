/**
 * 项目名称: titilink
 * 文件名称: AppLogger.java
 * Date: 2015/4/29
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
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
 * author by ganting
 * date 2015-04-29
 * since v1.0.0
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
