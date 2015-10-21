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

import java.util.Map;

/**
 * 描述：
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public class RestParameters<T> {

    private String url = null;

    private Object requestObj = null;

    private Map<String, String> customizedHeaders = null;

    private Class<T> responseClass = null;

    private byte[] reqBuffer = null;

    /**
     * @param url 目标url
     */
    public RestParameters(String url) {
        this(url, null, null, null, null);
    }

    /**
     * @param url               目标url
     * @param customizedHeaders 自定义header，如X-AUTH-USER-ID
     */
    public RestParameters(String url, Map<String, String> customizedHeaders) {
        this(url, null, customizedHeaders, null, null);
    }

    /**
     * @param url               目标url
     * @param customizedHeaders 自定义header，如X-AUTH-USER-ID
     * @param responseClass     要求封装的返回bean Class
     */
    public RestParameters(String url, Map<String, String> customizedHeaders, Class<T> responseClass) {
        this(url, null, customizedHeaders, responseClass, null);
    }

    /**
     * @param url        目标url
     * @param requestObj 请求的bean，Get和delete时不填
     */
    public RestParameters(String url, Object requestObj) {
        this(url, requestObj, null, null, null);
    }

    /**
     * @param url               目标url
     * @param requestObj        请求的bean，Get和delete时不填
     * @param customizedHeaders 自定义header，如X-AUTH-USER-ID
     */
    public RestParameters(String url, Object requestObj, Map<String, String> customizedHeaders) {
        this(url, requestObj, customizedHeaders, null, null);
    }

    /**
     * @param url               目标url
     * @param requestObj        请求的bean，Get和delete时不填
     * @param customizedHeaders 自定义header，如X-AUTH-USER-ID
     * @param responseClass     要求封装的返回bean Class
     */
    public RestParameters(String url, Object requestObj, Map<String, String> customizedHeaders, Class<T> responseClass) {
        this(url, requestObj, customizedHeaders, responseClass, null);
    }

    /**
     * @param url               目标url
     * @param requestObj        请求的bean，Get和delete时不填
     * @param customizedHeaders 自定义header，如X-AUTH-USER-ID
     * @param responseClass     要求封装的返回bean Class
     * @param reqBuffer         需要发送的Byte流
     * @since V1.0.3
     */
    public RestParameters(String url, Object requestObj, Map<String, String> customizedHeaders, Class<T> responseClass,
                          byte[] reqBuffer) {
        this.url = url;
        this.requestObj = requestObj;
        this.customizedHeaders = customizedHeaders;
        this.responseClass = responseClass;
        this.reqBuffer = reqBuffer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getRequestObj() {
        return requestObj;
    }

    public void setRequestObj(Object requestObj) {
        this.requestObj = requestObj;
    }

    public Map<String, String> getCustomizedHeaders() {
        return customizedHeaders;
    }

    public void setCustomizedHeaders(Map<String, String> customizedHeaders) {
        this.customizedHeaders = customizedHeaders;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    public byte[] getReqBuffer() {
        return reqBuffer;
    }

    public void setReqBuffer(byte[] reqBuffer) {
        this.reqBuffer = reqBuffer;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RestParameters [url=");
        builder.append(url);
        builder.append(", requestObj=");
        builder.append(requestObj);
        builder.append(", responseClass=");
        builder.append(responseClass);
        builder.append("]");
        return builder.toString();
    }

}
