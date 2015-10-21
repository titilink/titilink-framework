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

import com.titilink.camel.rest.common.RootResponse;

/**
 * 描述：
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class BeanRepresentation<T> extends RestRepresentation {
    private T response = null;

    private RootResponse rootResponse = null;

    public BeanRepresentation(int httpCode) {
        super(httpCode);
    }

    public BeanRepresentation(int httpCode, T response) {
        super(httpCode, null);
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public RootResponse getRootResponse() {
        return rootResponse;
    }

    public void setRootResponse(RootResponse rootResponse) {
        this.rootResponse = rootResponse;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BeanRepresentation [response=");
        builder.append(response);
        builder.append(", rootResponse=");
        builder.append(rootResponse);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}