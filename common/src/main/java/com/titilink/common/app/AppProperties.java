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
package com.titilink.common.app;

import java.io.*;
import java.util.Properties;

/**
 * <pre>
 * 应用配置文件[application.properties]目录资源读取器
 * 按照以下顺序及方式寻找此文件：
 * 1、从appContext里面查找定义，如果存在则按照定义路径加载（提供在程序设置路径的接口）
 * 2、在当前应用的工作目录下（相对路径）config子目录下寻找并加载
 * 3、在当前应用的classpath的config子目录下寻找并加载
 * </pre>
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class AppProperties {

    /**
     * application的属性信息
     */
    private static Properties appPpt = new Properties();

    /**
     *
     */
    private static final String RESOURCE_SYSCONFIG_MASK = "resource_CONFIG_MASK";

    static {
        String filenamePath;
        String fp = AppContext.getInstance().getAppHome();
        if (fp != null && fp.trim().length() > 0) {
            filenamePath = fp + "application.properties";
        } else {
            filenamePath = "config" + File.separator + "application.properties";
        }
        File f = new File(filenamePath);
        if (f.exists()) {
            FileInputStream bis = null;
            try {
                bis = new FileInputStream(f);
                appPpt.load(bis);
            } catch (Exception ex) {
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                }
                bis = null;
            }
        } else {
            InputStream is = null;
            try {
                is = ResourceLoader.getResAsStream(filenamePath);
                appPpt.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                is = null;
            }
        }
    }

    /**
     * 根据key获取值，并转成int类型返回
     *
     * @param key --key字符串
     * @return int --返回int类型值
     */
    public static int getAsInt(String key) {
        return Integer.parseInt(get(key).trim());
    }

    /**
     * 根据key获取值，并转成float类型返回
     *
     * @param key --key字符串
     * @return float --返回int类型值
     */
    public static float getAsFloat(String key) {
        return Float.parseFloat(get(key).trim());
    }

    /**
     * 根据key获取值，并转成float类型返回
     *
     * @param key          --key字符串
     * @param defaultValue --无法根据key获取值时，返回该默认值
     * @return float --返回int类型值
     */
    public static float getAsFloat(String key, float defaultValue) {
        String a = get(key);
        if (a == null || a.length() == 0) {
            return defaultValue;
        }
        return Float.parseFloat(get(key).trim());
    }

    /**
     * 根据key获取值，如果值不存在，则返回输入默认值
     *
     * @param key          --key字符串
     * @param defaultValue --无法根据key获取值时，返回该默认值
     * @return int --获得的int类型值
     */
    public static int getAsInt(String key, int defaultValue) {
        String a = get(key);
        if (a == null || a.length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(a.trim());
    }

    /**
     * 获取整个应用属性文件内容
     *
     * @return Properties --属性对象
     */
    public static Properties getProperties() {
        return appPpt;
    }

    /**
     * 根据key获取文件对应的值，以字符串类型返回
     *
     * @param key --指定key
     * @return String --属性值字符串
     */
    public static String get(String key) {
        return appPpt.getProperty(key);
    }

    /**
     * 通过字符集编码形式获取值 。application.properties文件默认编码为ansi，如果值为中文的话，
     * 可以charsetName=“8859_1”进行decode
     *
     * @param key         --指定key
     * @param charsetName --指定字符集
     * @return String --属性值字符串
     * @throws java.io.UnsupportedEncodingException --不支持指定字符集编码
     */
    public static String getByDecode(String key, String charsetName)
            throws UnsupportedEncodingException {
        String originStr = get(key);
        if (originStr == null) {
            return null;
        }
        return new String(originStr.getBytes(charsetName));
    }

    /**
     * 根据key获取值，如果这个值不存在，返回默认值
     *
     * @param key          --指定key
     * @param defaultValue --获取不到属性值时返回的默认值
     * @return String --属性值字符串
     */
    public static String get(String key, String defaultValue) {
        String a = get(key);
        if (a == null) {
            return defaultValue;
        }
        return a;
    }

    /**
     * 根据key获取值，并转成boolean类型返回
     *
     * @param key --指定key
     * @return boolean --用boolean类型表示的属性值
     */
    public static boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(get(key).trim());
    }

    /**
     * 根据key获取值，并转成boolean类型返回；如果获取不到，返回给定默认值
     *
     * @param key          --指定key
     * @param defaultValue --获取不到属性值时返回的默认值
     * @return boolean --用boolean类型表示的属性值
     */
    public static boolean getAsBoolean(String key, boolean defaultValue) {
        String a = get(key);
        if (a == null || a.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(a.trim());
    }

    public static int getCONFIGMASK() {
        return AppProperties.getAsInt(RESOURCE_SYSCONFIG_MASK, 0);
    }

    public static String getAppHome() {
        return AppContext.getInstance().getAppHome();
    }

}
