/**
 * 项目名称: titilink
 * 文件名称: SilvanMain.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.silvan.resource;

import com.titilink.common.log.AppLogger;
import com.titilink.silvan.common.SilvanResponse;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * silvan demo 资源类
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class DemoResource extends ServerResource {

    private static final AppLogger LOGGER = AppLogger.getInstance(DemoResource.class);

    /**
     * Demo资源的Get请求
     * the uri like /silvan/rest/v1.0/demo
     *
     * @return Silvan默认响应
     */
    @Get
    public SilvanResponse listDemo() {

        SilvanResponse response = new SilvanResponse();
        return response;
    }
}
