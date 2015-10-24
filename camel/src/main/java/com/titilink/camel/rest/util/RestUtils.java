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
package com.titilink.camel.rest.util;

import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 * Rest工具类
 *
 * @author kam
 * @date 2015/10/22
 * @since v1.0.0
 */
public final class RestUtils {

    public static <T> void buildResponse(Class<T> clazz, Response response, Status status) {
        buildResponse(clazz, response, status, null, null);
    }

    public static <T> void buildResponse(Class<T> clazz, Response response, Status status,
            Throwable throwable) {
        buildResponse(clazz, response, status, throwable, null);
    }

    public static <T> void buildResponse(Class<T> clazz, Response response, Status status,
                                         String message) {
        buildResponse(clazz, response, status, null, message);
    }

    public static <T> void buildResponse(Class<T> clazz, Response response, Status status,
            Throwable throwable, String message) {
        response.setStatus(status, throwable, message);
        Representation representation = new JacksonRepresentation(response);
        response.setEntity(representation);
    }

}
