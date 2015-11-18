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
package com.titilink.silvan.rest;

import com.titilink.camel.rest.common.ApplicationPlugin;
import com.titilink.common.log.AppLogger;
import com.titilink.silvan.util.XmlUtils;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * silvan openapi路由器
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class SilvanRouterPluginImpl implements ApplicationPlugin {

    private static final AppLogger LOGGER = AppLogger.getInstance(SilvanRouterPluginImpl.class);

    /**
     * Constant for URL parsing
     */
    private static final String SLASH = "/";

    /**
     * silvan rest api配置文件
     */
    private static final String SILVAN_REST_API_CONFIG = "config" + File.separator + "silvan_rest_api.xml";

    @Override
    public void attachTo(Router router) {
        try {
            this.initializeComponent(router);
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
        LOGGER.debug("Router: {}", router);
    }

    /**
     * 从配置文件中获取Rest api接口定义，然后注册
     *
     * @param router
     * @throws java.io.IOException
     */
    private void initializeComponent(Router router) throws IOException {
        LOGGER.debug("initializeComponent enter! ");

        InputStream is = Thread.currentThread().getContextClassLoader().
                getResourceAsStream(SILVAN_REST_API_CONFIG);

        ApiList apiList = XmlUtils.toBean(is, ApiList.class);

        if (apiList == null) {
            return;
        }
        try {
            attachApplicationServices(router, apiList);
        } catch (Throwable e) {
            LOGGER.error("attachApplicationServices failed.", e);
        }
        LOGGER.debug("initializeComponent exit!");
    }

    private void attachApplicationServices(Router router, ApiList apiList) {
        LOGGER.debug("attachApplicationServices enter!");

        if (apiList.getApis() == null) {
            LOGGER.debug("attachApplicationServices api is null!");
            return;
        }

        for (SilvanApi api : apiList.getApis()) {
            LOGGER.debug("attachApplicationServices enter! api is {}", api);

            String resource = api.getResource();
            List<String> versions = api.getVersions();

            for (String version : versions) {
                String uri = api.getUriPrefix() + SLASH + version + api.getUri();
                try {
                    router.attach(uri, (Class<ServerResource>) Class.forName(resource));
                } catch (ClassNotFoundException e) {
                    LOGGER.error("attach error! resource is {}, exception is {}", resource, e);
                }
            }
        }
        LOGGER.debug("attachApplicationServices exit! router is {}", router.getRoutes());
    }

}
