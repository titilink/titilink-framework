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
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
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
