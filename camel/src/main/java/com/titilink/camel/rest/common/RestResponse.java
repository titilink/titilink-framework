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
package com.titilink.camel.rest.common;

import org.restlet.data.Status;

import java.util.Map;

/**
 * Rest请求响应
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class RestResponse {
    private Status status;
    private String text;
    private Map<String, String> headers;

    public RestResponse(Status status) {

    }

    public Status getStatus() {
        return status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addHeader(String name, String value) {

    }

    public String getText() {
        return text;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
