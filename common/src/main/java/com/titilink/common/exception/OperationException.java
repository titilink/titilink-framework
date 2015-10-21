/**
 * 项目名称: titilink
 * 文件名称: OperationException.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.common.exception;

/**
 * 公共业务异常类
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class OperationException extends Exception {

    private Object code;

    public OperationException(String errorCode) {

    }

    public OperationException(String invalidInputParameter, String s) {

    }

    public OperationException(int code, Object code1, String message) {

    }

    public OperationException(int code, String message) {

    }

    public Object getCode() {
        return code;
    }
}
