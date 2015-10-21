/**
 * 项目名称: titilink
 * 文件名称: AppContext
 * Date: 2015/4/29
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.common.app;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用上下文
 * <p>
 * author by ganting
 * date 2015-04-29
 * since v1.0.0
 */
public final class AppContext {

    /**
     * 上下文信息缓存
     */
    private static final ConcurrentHashMap<String, Object> TABLE = new ConcurrentHashMap<String, Object>();

    /**
     * 单例
     */
    private static AppContext instance = new AppContext();

    /**
     * APP根路径
     */
    private static final String APP_HOME_PATH = "application.home.path";

    private AppContext() {
        // NOP
    }

    /**
     * 设置应用根路径
     *
     * @param homePath --目标路径
     */
    public void setAppHomePath(String homePath) {
        bind(APP_HOME_PATH, homePath);
    }

    /**
     * 从上下文查找app home路径
     *
     * @return 返回应用的根路径
     */
    public String getAppHomePathDefineFromContext() {
        return (String) lookup(APP_HOME_PATH);
    }

    /**
     * 获取APP根路径
     *
     * @return 返回应用的根路径
     */
    public String getAppHome() {
        String fp = System.getProperty(APP_HOME_PATH);
        if (fp != null && fp.trim().length() > 0) {
            if (!fp.endsWith(File.separator)) {
                fp = fp + File.separator;
            }
            return fp;
        } else {
            String ap = (String) AppContext.getInstance().lookup(APP_HOME_PATH);
            if (ap != null && ap.trim().length() > 0) {
                if (!ap.endsWith(File.separator)) {
                    ap = ap + File.separator;
                }
                return ap;
            }
        }
        return "config" + File.separator;
    }

    /**
     * 从缓存中获取所有上下文的key值
     *
     * @return 返回所有上下文key值
     */
    public Enumeration<String> getContextKeys() {
        return TABLE.keys();
    }

    /**
     * 绑定应用路径
     *
     * @param name --表示应用路径的key
     * @param obj  --路径字符串对象
     */
    public void bind(String name, Object obj) {
        TABLE.put(name, obj);
    }

    /**
     * 清空应用路径
     */
    public void close() {
        if (!TABLE.isEmpty()) {
            TABLE.clear();
        }
    }

    /**
     * 获取缓存的上下文信息
     *
     * @return
     */
    public Map<String, Object> getEnvironment() {
        return TABLE;
    }

    /**
     * 获取应用的路径
     *
     * @param name --表示应用的key
     * @return Object --应用路径字符串对象
     */
    public Object lookup(String name) {
        return TABLE.get(name);
    }

    /**
     * 重绑定应用路径
     *
     * @param name --表示应用的key
     * @param obj  --应用路径字符串对象
     */
    public void rebind(String name, Object obj) {
        if (TABLE.containsKey(name)) {
            TABLE.remove(name);
        }
        TABLE.put(name, obj);
    }

    /**
     * 去除应用路径绑定
     *
     * @param name --表示应用的key
     */
    public void unbind(String name) {
        if (TABLE.containsKey(name)) {
            TABLE.remove(name);
        }
    }

    public static AppContext getInstance() {
        return instance;
    }

}
