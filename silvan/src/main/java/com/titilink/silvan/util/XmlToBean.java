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
import com.titilink.silvan.model.ApiList;

import java.io.InputStream;

/**
 * xml文件转化为bean对象
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class XmlToBean {

    private static final XmlToBean instance = new XmlToBean();

    private XmlToBean() {

    }

    /**
     * 获取单例类
     *
     * @return 返回XmlToBean的单例
     */
    public static XmlToBean getInstance() {
        return instance;
    }

    /**
     * 获取配置文件中的API信息
     *
     * @param is API的文件流
     * @return API信息
     */
    public ApiList getApiConfig(InputStream is) {
        XStream xstream = new XStream();
        ApiList apiList = (ApiList) xstream.fromXML(is);
        return apiList;
    }



}
