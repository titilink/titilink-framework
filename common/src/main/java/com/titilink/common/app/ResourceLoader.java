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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源加载类
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class ResourceLoader {

    /**
     * 默认类加载器
     */
    private static ClassLoader defaultClassLoader;

    /**
     * 获取jar文件中文本性资源的内容
     *
     * @param jarFileName --jar文件名(eg:D:\\tmp\\test.jar)
     * @param resUrl      --资源名称(eg:config/job.properties)
     * @return String --获取到的资源内容
     * @throws java.io.IOException --在读取文件过程中发生异常
     */
    public static String getResourceContentFromJarFile(String jarFileName, String resUrl)
            throws IOException {
        StringBuffer sb = new StringBuffer();
        JarFile jarFile = new JarFile(jarFileName);
        JarEntry je = jarFile.getJarEntry(resUrl);
        try {
            if (je != null) {
                readJar(sb, jarFile, je);
            }
        } finally {
            jarFile.close();
        }
        return sb.toString();
    }

    private static void readJar(StringBuffer sb, JarFile jarFile, JarEntry je) throws IOException {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = jarFile.getInputStream(je);
            br = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                } else {
                    sb.append(s);
                    sb.append('\n');
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
            je = null;
        }
    }

    /**
     * 从一个jar文件里面获取指定资源
     *
     * @param jarFileName --jar文件名(eg:D:\\tmp\\test.jar)
     * @param resUrl      --指定资源名(eg:config/job.properties)
     * @return Properties --指定资源的属性集
     */
    public static Properties getPropertiesResourceFromJarFile(String jarFileName, String resUrl) {
        InputStream is = null;
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarFileName);
            JarEntry je = jarFile.getJarEntry(resUrl);
            if (je != null) {
                is = jarFile.getInputStream(je);
                Properties p = new Properties();
                p.load(is);
                return p;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace(); //NOPMD
                }
            }
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace(); //NOPMD
                }
            }
        }
    }

    /**
     * 获取指定资源URL对象
     *
     * @param resource --指定的资源名称
     * @return URL --用于读取资源的 URL 对象
     * @throws java.io.IOException --找不到指定资源抛出该异常
     */
    public static URL getResURL(String resource) throws IOException {
        return getResURL(getClassLoader(), resource);
    }

    /**
     * 使用指定的类加载器获取指定资源URL对象
     *
     * @param loader   --指定的类加载器
     * @param resource --指定的资源名称
     * @return URL --用于读取资源的 URL 对象
     * @throws java.io.IOException --找不到指定资源抛出该异常
     */
    public static URL getResURL(ClassLoader loader, String resource) throws IOException {
        URL url = null;
        if (loader != null) {
            url = loader.getResource(resource);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /**
     * 获取类加载器
     *
     * @return ClassLoader --当前线程上下文类加载器
     */
    public static ClassLoader getClassLoader() {
        if (defaultClassLoader != null) {
            return defaultClassLoader;
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * 获取指定资源输入流
     *
     * @param resource --指定的资源
     * @return InputStream --指定资源的输入流
     * @throws java.io.IOException --找不到指定资源异常
     */
    public static InputStream getResAsStream(String resource) throws IOException {
        return getResAsStream(getClassLoader(), resource);
    }

    /**
     * 获取指定资源输入流
     *
     * @param loader   --指定的类加载器，用来加载指定资源
     * @param resource --指定的资源
     * @return InputStream --指定资源的输入流
     * @throws java.io.IOException --找不到指定资源异常
     */
    public static InputStream getResAsStream(ClassLoader loader, String resource)
            throws IOException {
        InputStream in = null;
        if (loader != null) {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null) {
            URL url = new URL(resource);
            in = url.openStream();
        }
        return in;
    }

    public static void setDefaultClassLoader(ClassLoader dcl) {
        defaultClassLoader = dcl;
    }

}
