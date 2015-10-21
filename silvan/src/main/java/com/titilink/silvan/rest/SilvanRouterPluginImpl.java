/**
 * 项目名称: titilink
 * 文件名称: SilvanRouterPluginImpl.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.silvan.rest;

import com.titilink.camel.rest.common.ApplicationPlugin;
import com.titilink.common.log.AppLogger;
import com.titilink.silvan.model.ApiList;
import com.titilink.silvan.model.SilvanApi;
import com.titilink.silvan.util.XmlToBean;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * silvan openapi路由器
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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

        ApiList apiList = XmlToBean.getInstance().getApiConfig(is);

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
