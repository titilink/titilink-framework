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
package com.titilink.silvan.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * xml工具类，通过xstream实现xml和bean之间的互相转化
 *
 * @author by kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public final class XmlUtils {

    public static <T> T toBean(String xmlStr, Class<T> clazz) {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(clazz);
        T obj = (T) xStream.fromXML(xmlStr);
        return obj;
    }

    public static <T> T toBean(File file, Class<T> clazz) {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(clazz);
        T obj = (T) xStream.fromXML(file);
        return obj;
    }

    public static <T> T toBean(InputStream is, Class<T> clazz) {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(clazz);
        T obj = (T) xStream.fromXML(is);
        return obj;
    }

    public static <T> String toXml(T t) {
        XStream xStream = new XStream(new DomDriver());
        return xStream.toXML(t);
    }

}
