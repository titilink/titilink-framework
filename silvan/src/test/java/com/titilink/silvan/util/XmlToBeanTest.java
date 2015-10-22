/**
 * Copyright 2005-2015 titilink
 * <p/>
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * <p/>
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * <p/>
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * <p/>
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * <p/>
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * <p/>
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * <p/>
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * <p/>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 * <p/>
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.silvan.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.titilink.silvan.model.ApiList;
import com.titilink.silvan.model.SilvanApi;
import org.junit.Test;
import sun.security.krb5.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：[描述]
 * @author kam
 * @date 2015/10/21
 * @since [版本号]
 */
public class XmlToBeanTest {

    @Test
    public void testXml2Bean() throws FileNotFoundException {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        ConfigReader reader = (ConfigReader) xstream.fromXML(
                new FileInputStream(new File("E:\\photo\\config.xml")));
        System.out.println(reader);
    }

    @Test
    public void testBean2Xml() {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        SilvanApi api = new SilvanApi();
        api.setUriPrefix("1");
        api.setUri("2");
        api.setResource("3");
        List<SilvanApi> apis = new ArrayList<>(1);
        apis.add(api);
        ApiList apiList = new ApiList();
        apiList.setApis(apis);
        String xml = xstream.toXML(apiList);
        System.out.println(xml);
        ApiList apiList1 = (ApiList) xstream.fromXML(xml);
        System.out.println(apiList1);
    }

}
