/**
 * 项目名称: titilink
 * 文件名称: RestRepresentation.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.params;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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
