/**
 * 项目名称: titilink
 * 文件名称: RootResponse.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.common;

/**
 * 根节点的返回响应
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class RootResponse {

    private String code;

    private String message;

    public RootResponse(String code, String message) {

    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
