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
package com.titilink.camel.rest.client;

import com.titilink.camel.rest.common.RestResponse;
import com.titilink.camel.rest.common.RootResponse;
import com.titilink.camel.rest.params.AdditionalParameters;
import com.titilink.camel.rest.params.BeanRepresentation;
import com.titilink.camel.rest.params.RestParameters;
import com.titilink.camel.rest.util.CommonCode;
import com.titilink.camel.rest.util.ConvertionUtil;
import com.titilink.common.exception.OperationException;
import com.titilink.common.log.AppLogger;
import org.apache.commons.lang.StringUtils;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Rest客户端
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class RestClient {

    private static final AppLogger LOGGER = AppLogger.getInstance(RestClient.class);

    private static final String GET = "GET";

    private static final String POST = "POST";

    private static final String PUT = "PUT";

    private static final String DELETE = "DELETE";

    private static final int HTTP_READ_TIME = 300000;

    /**
     * 发送get请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可以传入null
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> get(RestParameters<T> params, AdditionalParameters additionalParams)
            throws OperationException {
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, GET);

        LOGGER.debug("[GET] SUCCEED. ");
        return represent;
    }

    /**
     * 发送get请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可以传入null
     * @param msTimeout        超时设置，单位ms
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> get(RestParameters<T> params, AdditionalParameters additionalParams, long msTimeout)
            throws OperationException {
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, GET, msTimeout);

        LOGGER.debug("[GET] SUCCEED. ");
        return represent;
    }

    /**
     * 发送POST请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> post(RestParameters<T> params, AdditionalParameters additionalParams)
            throws OperationException {
        BeanRepresentation<T> represent = post(params, additionalParams, HTTP_READ_TIME);

        LOGGER.debug("[POST] SUCCEED. ");
        return represent;
    }

    /**
     * 发送POST请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @param msTimeout        超时设置，单位ms
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> post(RestParameters<T> params, AdditionalParameters additionalParams, long msTimeout)
            throws OperationException {
        beanToJason(params);
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, POST, msTimeout);

        LOGGER.debug("[POST] SUCCEED. ");
        return represent;
    }

    /**
     * 发送PUT请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> put(RestParameters<T> params, AdditionalParameters additionalParams)
            throws OperationException {
        BeanRepresentation<T> represent = put(params, additionalParams, HTTP_READ_TIME);

        LOGGER.debug("[PUT] SUCCEED. ");
        return represent;
    }

    /**
     * 发送PUT请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @param msTimeout        超时设置，单位ms
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> put(RestParameters<T> params, AdditionalParameters additionalParams, long msTimeout)
            throws OperationException {
        beanToJason(params);
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, PUT, msTimeout);

        LOGGER.debug("[PUT] SUCCEED. ");
        return represent;
    }

    /**
     * 发送DELETE请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> delete(RestParameters<T> params, AdditionalParameters additionalParams)
            throws OperationException {
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, DELETE);

        LOGGER.debug("[DELETE] SUCCEED. ");
        return represent;
    }

    /**
     * 发送DELETE请求
     *
     * @param params           基本参数，如url，自定义header，返回bean的class等
     * @param additionalParams 扩展参数，没有扩展参数可传入null
     * @param msTimeout        超时设置，单位ms
     * @return BeanRepresentation
     * @throws OperationException
     */
    public static <T> BeanRepresentation<T> delete(RestParameters<T> params, AdditionalParameters additionalParams,
                                                   long msTimeout)
            throws OperationException {
        BeanRepresentation<T> represent = handleRequest(params, additionalParams, DELETE, msTimeout);

        LOGGER.debug("[DELETE] SUCCEED. ");
        return represent;
    }

    public static <T> BeanRepresentation<T> handleRequest(RestParameters<T> params,
                                                          AdditionalParameters additionalParams, String method, long msTimeout)
            throws OperationException {
        // additionalParams 以及 params.customizedHeaders可以为null
        if (params == null || StringUtils.isBlank(params.getUrl())) {
            LOGGER.error("handleRequest FAILED! invalid RestParameters");
            OperationException oe = new OperationException(400, CommonCode.INVALID_INPUT_PARAMETER,
                    "Invalid parameter.");
            throw oe;
        }

        //get byte
        byte[] byteReq = null;
        if (params.getReqBuffer() == null) {
            if (params.getRequestObj() != null) {
                byteReq = params.getRequestObj().toString().getBytes(Charset.defaultCharset());
            }
        } else {
            byteReq = params.getReqBuffer();
        }

        // construct URI
        URI requestUri = toURI(params.getUrl());

        // post
        RestResponse result = null;
        try {
            Map<String, String> customizedHeaders = params.getCustomizedHeaders();
            ChallengeResponse challengeResponse = null;
            if (additionalParams != null) {
                challengeResponse = additionalParams.getChallengeResponse();
            }
            switch (method) {
                case GET: {
                    result = CamelClient.handleGet(requestUri, customizedHeaders, challengeResponse, msTimeout);
                    break;
                }
                case POST: {
                    result =
                            CamelClient.handlePost(requestUri, byteReq, customizedHeaders, challengeResponse, msTimeout);
                    break;
                }
                case PUT: {
                    result =
                            CamelClient.handlePut(requestUri, byteReq, customizedHeaders, challengeResponse, msTimeout);
                    break;
                }
                case DELETE: {
                    result = CamelClient.handleDelete(requestUri, customizedHeaders, challengeResponse, msTimeout);
                    break;
                }
                default: {
                    LOGGER.debug("call default request method:{}", method);
                    result = CamelClient.handleDefault(method, requestUri, byteReq, customizedHeaders, challengeResponse, msTimeout);
                    break;
                }
            }
        } catch (Throwable t) {
            // 为了保护调用REST接口由于对端处理不妥而抛出异常此处增加捕获保护
            LOGGER.error("[{}] handleRequest FAILED", method, t);
            OperationException oe = new OperationException(CommonCode.INVOKE_INTERFACE_EXCEPTION,
                    "CamelClient throws exception");
            throw oe;
        }

        if (null == result || result.getStatus() == null) {
            LOGGER.error("[{}] handleRequest FAILED, return null", method);
            OperationException oe = new OperationException(CommonCode.INVOKE_INTERFACE_EXCEPTION,
                    "handleRequest return null.");
            throw oe;
        }

        String text = result.getText();
        Status status = result.getStatus();
        int code = status.getCode();

        BeanRepresentation<T> represent = new BeanRepresentation<T>(code);
        if (null != result.getHeaders()) {
            represent.addAllHeader(result.getHeaders());
        }

        represent.setText(text);
        if (text != null && params.getResponseClass() != String.class) {
            // text有内容
            if (status.isSuccess()) {
                // 标准格式，即调用方提供了非空的响应类型
                if (params.getResponseClass() != null) {
                    T response = ConvertionUtil.convertJson2Bean(text, params.getResponseClass());
                    if (null != response) {
                        represent.setResponse(response);
                    } else {
                        LOGGER.error("[{}] handleRequest FAILED, json2bean return null", method);
                        OperationException oe =
                                new OperationException(code, CommonCode.INVOKE_INTERFACE_EXCEPTION, text);
                        throw oe;
                    }
                }
            } else {
                // 检查是否返回的是RootResponse结构（即只返回了code和message）
                RootResponse rootResp = ConvertionUtil.convertJson2Bean(text, RootResponse.class);
                if (rootResp != null) {
                    represent.setRootResponse(rootResp);
                } else {
                    LOGGER.error("[{}] handleRequest FAILED, json2bean return null", method);
                    OperationException oe = new OperationException(code,
                            CommonCode.INVOKE_INTERFACE_EXCEPTION, text);
                    throw oe;
                }
            }
        }

        return represent;
    }

    public static <T> BeanRepresentation<T> handleRequest(RestParameters<T> params,
                                                          AdditionalParameters additionalParams, String method)
            throws OperationException {
        return handleRequest(params, additionalParams, method, HTTP_READ_TIME);
    }

    private static URI toURI(String url) throws OperationException {
        URI requestUri = null;
        try {
            requestUri = new URI(url);
        } catch (URISyntaxException e) {
            LOGGER.error("new URI FAILED. Illegal url.", e);
            OperationException oe = new OperationException(400, CommonCode.INVALID_INPUT_PARAMETER, "Illegal url");
            throw oe;
        }
        return requestUri;
    }

    private static String toJson(Object requestObj) throws OperationException {
        String jsonReq = null;
        jsonReq = ConvertionUtil.convertBean2Json(requestObj);
        if (null == jsonReq) {
            // 因安全原因，不能打印request的内容
            LOGGER.error("Invalid requestObj, could not convert it to json.");
            OperationException oe = new OperationException(400, CommonCode.INVALID_INPUT_PARAMETER,
                    "Cannot convert request to json format.");
            throw oe;
        }
        return jsonReq;
    }

    @SuppressWarnings("rawtypes")
    private static void beanToJason(RestParameters params) throws OperationException {
        // bean to json
        if (params != null) {
            if (params.getReqBuffer() == null && params.getRequestObj() != null) {
                String jsonReq = null;
                // 如果请求体为String类型，直接为jsonReq不需要调用toJson
                if (params.getRequestObj() instanceof String) {
                    jsonReq = (String) (params.getRequestObj());
                } else {
                    jsonReq = toJson(params.getRequestObj());
                }
                params.setReqBuffer(jsonReq.getBytes(Charset.defaultCharset()));
            }
        }
    }

}
