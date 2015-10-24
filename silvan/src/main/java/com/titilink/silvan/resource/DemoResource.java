package com.titilink.silvan.resource;

import com.titilink.silvan.common.SilvanResponse;
import com.titilink.silvan.resource.model.DemoRequest;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * 描述：[描述]
 *
 * @author kam
 * @date 2015/10/24
 * @since [版本号]
 */
public interface DemoResource {

    /**
     * Demo资源的Get请求
     * the uri like /silvan/rest/v1.0/demo
     *
     * @return Silvan默认响应
     */
    @Get
    SilvanResponse listDemo();

    /**
     * Demo资源的Post请求
     * the uri like /silvan/rest/v1.0/demo
     *
     * @return Silvan默认响应
     */
    @Post
    SilvanResponse addDemo(DemoRequest request);

}
