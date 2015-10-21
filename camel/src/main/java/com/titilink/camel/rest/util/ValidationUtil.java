/**
 * 项目名称: titilink
 * 文件名称: ValidationUtil.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.util;

import com.titilink.camel.rest.common.RootResponse;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

/**
 * 公共校验类
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public final class ValidationUtil {

    /**
     * 注解 message模板分隔符
     */
    public final static String MESSAGE_TEMPLATE_SEPARATOR = "##";

    private static <T> Set<ConstraintViolation<T>> validateBean(T instance, Class<?>... groups) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> result = validator.validate(instance, groups);
        return result;
    }

    /**
     * 提供基于JSR-303的bean校验
     *
     * @param instance 需校验的对象实例
     * @return
     */
    public static <T> Set<String> validate(T instance, Class<?>... groups) {
        Set<ConstraintViolation<T>> set = validateBean(instance, groups);

        Set<String> errorCodeSet = new HashSet<String>(set.size());
        for (ConstraintViolation<T> c : set) {
            errorCodeSet.add(c.getMessage());
        }

        return errorCodeSet;
    }

    /**
     * 提供基于JSR-303的bean校验
     *
     * @param instance 需校验的对象实例
     * @return
     */
    public static <T> Set<RootResponse> validateExtend(T instance, Class<?>... groups) {
        Set<ConstraintViolation<T>> set = validateBean(instance, groups);

        if (set.size() == 0) {
            return null;
        }

        Set<RootResponse> errorBoxs = new HashSet<RootResponse>(set.size());
        for (ConstraintViolation<T> c : set) {
            String nessage = c.getMessage();
            if (StringUtils.isNotEmpty(nessage)) {
                String[] item = nessage.split(MESSAGE_TEMPLATE_SEPARATOR);
                errorBoxs.add(new RootResponse(item[0], item[1]));
            }
        }
        return errorBoxs;
    }

    /**
     * 构造函数
     */
    private ValidationUtil() {
        // nothing
    }

}
