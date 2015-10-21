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
package com.titilink.camel.rest.connector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Http Connector连接器，用于实现Netty与Restlet、Jersey或者其他Rest
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public interface HttpConnector {

    /**
     * 处理netty请求
     *
     * @param ctx netty请求上下文
     * @param request netty http request
     */
    void service(ChannelHandlerContext ctx, FullHttpRequest request);

}
