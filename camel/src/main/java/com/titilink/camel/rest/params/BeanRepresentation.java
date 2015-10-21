/**
 * 项目名称: titilink
 * 文件名称: BeanRepresentation.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.params;

import com.titilink.camel.rest.common.RootResponse;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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