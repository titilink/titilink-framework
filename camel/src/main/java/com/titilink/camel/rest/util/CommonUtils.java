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
import com.titilink.common.exception.OperationException;
import com.titilink.common.log.AppLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.restlet.data.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 通用工具类
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class CommonUtils {

    private static final AppLogger LOGGER = AppLogger.getInstance(CommonUtils.class);

    /**
     * 需要校验Request的包名前缀
     */
    private static final String PACKAGE_NAME_PREFIX = "com.titilink";

    /**
     * 校验时需要过滤的属性名
     */
    public static final String NEED_IGNORE_FIELD = "serialVersionUID";

    /**
     * request请求中action的数量
     */
    public static final int ACTION_COUNT_ONE = 1;

    /**
     * 日期格式
     */
    public static final String[] DATE_FORMAT = new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss"};

    /**
     * 普通Request校验</br>
     * 去掉前后空格且根据正则表达式校验请求参数字段，仅支持list集合，其他集合暂时不支持</br>
     *
     * @param request
     * @param <T>
     * @throws OperationException
     */
    public static <T> void checkRequest(T request) throws OperationException {
        if (null == request) {
            LOGGER.error("checkRequest FAILED! request is null");
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
        }

        // 通过注解校验入参
        trimAndCheckParameter(request);

        LOGGER.debug("checkRequest SUCCEED! all parameters are vaild");
        return;
    }

    /**
     * 校验操作Request</br>
     * 去掉前后空格且根据正则表达式校验请求参数字段，仅支持list集合，其他集合暂时不支持</br>
     *
     * @param request
     * @param <T>
     * @throws OperationException
     */
    public static <T> void checkRequest4Action(T request) throws OperationException {
        if (null == request) {
            LOGGER.error("checkRequest4Action FAILED! request is null");
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
        }

        // 通过注解校验入参
        trimAndCheckParameter(request);

        // 判断action是否仅有一个
        checkActionCount(request);

        LOGGER.debug("checkRequest4Action SUCCEED! all parameters are vaild");
        return;
    }

    /**
     * 将字符串转化为数字
     *
     * @param input
     * @return
     * @throws OperationException
     */
    public static int parseInt(String input) throws OperationException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            LOGGER.error("parseInt method error, invalid input parameter. input={}.", input);
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER);
        }
    }

    /**
     * 将字符串转化为日期对象
     *
     * @param input
     * @return
     * @throws OperationException
     */
    public static Date parseDate(String input) throws OperationException {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        try {
            return DateUtils.parseDate(input, DATE_FORMAT);
        } catch (ParseException e) {
            LOGGER.error("parseDate method error, invalid input parameter. input={}.", input);
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "parse date error");
        }
    }

    /**
     * 判断翻页参数是否正确
     *
     * @param start
     * @param limit
     * @return
     */
    public static boolean isValidPaginationPara(Integer start, Integer limit) {
        if (start == null || limit == null) {
            return false;
        }
        if (start < 0 || limit < 0) {
            return false;
        }
        return true;
    }

    /**
     * clone 实现序列化的对象 （内存级复制）
     *
     * @param obj 源对象
     * @return 返回克隆后的对象
     */
    public static Object clone(Object obj) {
        if (obj == null) {
            return null;
        }

        Object anotherObj = null;
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bytes = baos.toByteArray();
        } catch (Exception ex) {
            LOGGER.error("CloneObjectUtil cloneexception ", ex);
            return null;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    LOGGER.error("CloneObjectUtil cloneexception ", e);
                }
            }
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            anotherObj = ois.readObject();
        } catch (Exception ex) {
            LOGGER.error("CloneObjectUtil cloneexception ", ex);
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (Exception e) {
                    LOGGER.error("CloneObjectUtil cloneexception ", e);
                }
            }
        }
        return anotherObj;
    }

    /**
     * 去掉空格并校验参数
     *
     * @param instance
     * @param <T>
     * @throws OperationException
     */
    private static <T> void trimAndCheckParameter(T instance)
            throws OperationException {
        if (null == instance) {
            return;
        }

        if ((!instance.getClass().getName().startsWith(PACKAGE_NAME_PREFIX)) && instance.getClass() != List.class) {
            return;
        }

        Field[] fields = instance.getClass().getDeclaredFields();
        String value = null;
        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.getType().getName().startsWith(PACKAGE_NAME_PREFIX)) {
                    trimAndCheckParameter(field.get(instance));
                } else if (field.getType() == String.class) {
                    value = (String) field.get(instance);
                    if (null != value) {
                        field.set(instance, value.trim());
                    }
                } else if (field.getType() == List.class) {
                    List<T> list = (List<T>) field.get(instance);
                    if (null != list) {
                        for (T t : list) {
                            trimAndCheckParameter(t);
                        }
                    }
                }
            } catch (OperationException e) {
                LOGGER.error("trimAndCheckParameter method error, trim exception field={}, instance={}",
                        field, instance);

                LOGGER.error("trimAndCheckParameter method error, trim exception e=", e);
                throw new OperationException(Status.CLIENT_ERROR_BAD_REQUEST.getCode(),
                        e.getErrorCode(), e.getMessage());
            } catch (Exception e) {
                LOGGER.error("trimAndCheckParameter method error, trim exception field={}, instance={}",
                        field, instance);
                LOGGER.error("trimAndCheckParameter method error, trim exception e=", e);
                throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
            }
        }

        // 注解方式校验参数
        checkParameter(instance);
    }

    /**
     * 校验参数
     *
     * @param instance
     * @param <T>
     * @throws OperationException
     */
    private static <T> void checkParameter(T instance)
            throws OperationException {
        Set<RootResponse> errorBoxs = ValidationUtil.validateExtend(instance);


        if (null != errorBoxs) {
            Iterator<RootResponse> iterator = errorBoxs.iterator();
            while (iterator.hasNext()) {
                RootResponse response = (RootResponse) iterator.next();
                throw new OperationException(response.getCode(), response.getMessage());
            }

        }
    }

    /**
     * 校验request中action数量
     *
     * @param instance
     * @param <T>
     * @throws OperationException
     */
    private static <T> void checkActionCount(T instance) throws OperationException {
        if (null == instance) {
            LOGGER.error("checkActionCount FAILED! request is null");
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
        }

        int actionCount = 0;
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (NEED_IGNORE_FIELD.equals(field.getName())) {
                continue;
            }

            try {
                if (null != field.get(instance)) {
                    ++actionCount;
                }
            } catch (Exception e) {
                LOGGER.error("checkActionCount exception for req:" + instance, e);
                throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
            }
        }

        if (ACTION_COUNT_ONE != actionCount) {
            LOGGER.error("checkActionCount FAILED! the number of action in request:{} is not " +
                    ACTION_COUNT_ONE, instance);
            throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
        }
    }

    /**
     * 返回第一个出错的信息
     *
     * @param instance
     * @throws OperationException
     */
    private static <T> void checkParameterWithRawInfo(T instance) throws OperationException {
        Set<RootResponse> errorBoxs = ValidationUtil.validateExtend(instance);
        if (null != errorBoxs) {
            LOGGER.error("checkParameterWithRawInfo method error, instance={}, errorBoxs={}.",
                    new Object[]{instance, errorBoxs});
            Iterator<RootResponse> it = errorBoxs.iterator();
            if (null != it && it.hasNext()) {
                RootResponse response = it.next();
                throw new OperationException(response.getCode(), response.getMessage());
            } else {
                throw new OperationException(CommonCode.INVALID_INPUT_PARAMETER, "Invalid input params");
            }
        }
    }

}
