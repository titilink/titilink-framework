/**
 * 项目名称: titilink
 * 文件名称: RestResponse.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.common;

import org.restlet.data.Status;

import java.util.Map;

/**
 * Rest请求响应
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
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
