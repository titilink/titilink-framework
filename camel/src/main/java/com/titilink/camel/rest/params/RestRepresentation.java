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
package com.titilink.camel.rest.params;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class RestRepresentation {

    private int httpCode = -1;

    private Map<String, String> headers = new HashMap<String, String>();

    private String text = null;

    public RestRepresentation(int httpCode) {
        this.httpCode = httpCode;
    }

    /**
     *
     */
    public RestRepresentation(int httpCode, String text) {
        this.httpCode = httpCode;
        this.text = text;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addAllHeader(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RestRepresentation{");
        sb.append("httpCode=").append(httpCode);
        sb.append(", headers size=").append(headers.size());
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
