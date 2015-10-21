/**
 * 项目名称: titilink
 * 文件名称: XmlToBean.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.silvan.util;

import com.thoughtworks.xstream.XStream;
import com.titilink.silvan.model.ApiList;

import java.io.InputStream;

/**
 * xml文件转化为bean对象
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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
