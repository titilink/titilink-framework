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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.titilink.common.exception.OperationException;
import com.titilink.common.log.AppLogger;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 转换方法的工具类，提供各种转换的工具方法
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class ConvertionUtil {

    private static final AppLogger LOGGER = AppLogger.getInstance(ConvertionUtil.class);

    /**
     * sql的escape语句定义，与convertParams方法配合使用，用于拼接
     */
    public static final String SQL_ESCAPE_EXPRESSION = " escape '$' ";

    /**
     * A normal decimal kilo is 1000
     */
    private static final BigDecimal DECIMAL_KILO = new BigDecimal("1000");

    /**
     * A binary kilo stands for 1024
     */
    private static final BigDecimal BINARY_KILO = new BigDecimal("1024");

    private ConvertionUtil() {

    }

    /**
     * Convert a decimal MB(megabyte) to decimal GB(gigabyte)<br>
     * <br>
     * <p>
     * Officially, there are <b>1000 (10^3)</b> megabytes (MB) in a gigabyte
     * (GB)<br>
     * A Memory manufacturer definition is <b>1024 (2^10)</b> megabytes per
     * gigabyte, but this is more properly called a <b>gigabinary byte
     * (GiB)</b>, sometimes contracted to <b>gibibyte</b>. <br>
     * <br>
     * 1,000 megabyte (MB) = 1 gigabyte (GB)<br>
     * 1,024 mebibyte (MiB) = 1 gibibyte (GiB)<br>
     *
     * @param valueMB
     * @return GB
     */
    public static double convertMB2GB(int valueMB) {
        return (new BigDecimal(valueMB).divide(DECIMAL_KILO).doubleValue());
    }

    /**
     * Convert a binary MiB(mebibyte) to binary GiB(gibibyte)<br>
     * <br>
     * <p>
     * Officially, there are <b>1000 (10^3)</b> megabytes (MB) in a gigabyte
     * (GB)<br>
     * A Memory manufacturer definition is <b>1024 (2^10)</b> megabytes per
     * gigabyte, but this is more properly called a <b>gigabinary byte
     * (GiB)</b>, sometimes contracted to <b>gibibyte</b>. <br>
     * <br>
     * 1,000 megabyte (MB) = 1 gigabyte (GB)<br>
     * 1,024 mebibyte (MiB) = 1 gibibyte (GiB)<br>
     *
     * @param valueMiB
     * @return GiB
     */
    public static double convertMiB2GiB(int valueMiB) {
        return (new BigDecimal(valueMiB).divide(BINARY_KILO).doubleValue());
    }

    /**
     * 提供将JSON格式字符串转换为bean类的工具方法
     */
    public static <T> T convertJson2Bean(String json, Class<T> clazz) {
        if (null == json || null == clazz) {
            LOGGER.error("convertJson2Bean FAILED, jsonStr={}, bean-class={}",
                    json, clazz);
            return null;
        }

        ObjectMapper mp = new ObjectMapper();
        mp.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        mp.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mp.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        T bean = null;
        try {
            bean = mp.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error(
                    "convertJson2Bean FAILED! Exception while json to bean:", e);
        }
        return bean;
    }

    /**
     * 提供将bean类转换为json格式字符串的工具方法
     */
    public static <T> String convertBean2Json(T bean) {
        if (null == bean) {
            LOGGER.error("convertBean2Json FAILED, bean-class is null.");
            return null;
        }

        ObjectMapper mp = new ObjectMapper();
        mp.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mp.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mp.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

        String ret = "";
        try {
            ret = mp.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            LOGGER.error(
                    "convertBean2Json FAILED! Exception while bean to json:", e);
        }
        return ret;
    }

    /**
     * 字符型数字转化成长整型数字 转换出现异常时返回-9223372036854775808 ，请注意使用
     */
    public static long convertLong(String number, String errorCode)
            throws OperationException {
        try {
            return Long.valueOf(number);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to format string to long: " + number, e);
            throw new OperationException(errorCode);
        } catch (Exception e) {
            LOGGER.error("Failed to format string to long: " + number, e);
            throw new OperationException(errorCode);
        }
    }

    /**
     * 字符型boolean值转化成boolean类型 转换出现异常时抛错误码
     */
    public static boolean toBoolean(String value, String errorCode)
            throws OperationException {
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            LOGGER.error("Failed to format string to boolean: " + value, e);
            throw new OperationException(errorCode);
        }
    }

    /**
     * 字符型数字转化成整型数字 转换出现异常时返回-2147483648，请注意使用
     */
    public static int convertInt(String number, String errorCode)
            throws OperationException {
        int result = Integer.MIN_VALUE;

        try {
            result = Integer.valueOf(number);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to format string to integer: " + number, e);
            throw new OperationException(errorCode);
        } catch (Exception e) {
            LOGGER.error("Failed to format string to integer: " + number, e);
            throw new OperationException(errorCode);
        }

        return result;
    }

    public static boolean containsXSSChar(String text) {
        if (text == null) {
            return false;
        }
        return text.indexOf('<') >= 0 || text.indexOf('>') >= 0
                || text.indexOf("\\u003c") >= 0 || text.indexOf("\\u003e") >= 0
                || text.indexOf("\\u003C") >= 0 || text.indexOf("\\u003E") >= 0;
    }

    /**
     * 如果request中数据保存在流里，为了支持多次读取请求体， 需要将流转换为text
     *
     * @param request
     */
    public static String toTextEntity(Request request) {
        String text = null;
        try {
            // 从tomcat转过来的request里数据存在流里，不能多次读取
            // 把流里的数据获取出来
            Representation entity = request.getEntity();
            if (entity == null) {
                return null;
            }

            // 先检查是否非文本，非文本类型，不做转换
            MediaType mtype = entity.getMediaType();
            if (!isConvertible(mtype)) {
                return null;
            }

            text = entity.getText();
            // 把原有流释放
            entity.exhaust();

            if (text != null) {
                request.setEntity(text, MediaType.APPLICATION_JSON);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return text;
    }

    private static boolean isConvertible(MediaType mtype) {
        return MediaType.APPLICATION_ALL_JSON.equals(mtype)
                || MediaType.APPLICATION_ALL_XML.equals(mtype)
                || MediaType.APPLICATION_JSON.equals(mtype)
                || MediaType.APPLICATION_JSON_ACTIVITY.equals(mtype)
                || MediaType.APPLICATION_JSON_PATCH.equals(mtype)
                || MediaType.APPLICATION_JSON_SMILE.equals(mtype)
                || MediaType.APPLICATION_W3C_SCHEMA.equals(mtype)
                || MediaType.APPLICATION_W3C_XSLT.equals(mtype)
                || MediaType.APPLICATION_WWW_FORM.equals(mtype)
                || MediaType.APPLICATION_XHTML.equals(mtype)
                || MediaType.APPLICATION_XMI.equals(mtype)
                || MediaType.APPLICATION_XML.equals(mtype)
                || MediaType.APPLICATION_XML_DTD.equals(mtype)
                || MediaType.TEXT_ALL.equals(mtype)
                || MediaType.TEXT_CALENDAR.equals(mtype)
                || MediaType.TEXT_CSS.equals(mtype)
                || MediaType.TEXT_CSV.equals(mtype)
                || MediaType.TEXT_DAT.equals(mtype)
                || MediaType.TEXT_HTML.equals(mtype)
                || MediaType.TEXT_J2ME_APP_DESCRIPTOR.equals(mtype)
                || MediaType.TEXT_JAVASCRIPT.equals(mtype)
                || MediaType.TEXT_PLAIN.equals(mtype)
                || MediaType.TEXT_RDF_N3.equals(mtype)
                || MediaType.TEXT_RDF_NTRIPLES.equals(mtype)
                || MediaType.TEXT_TSV.equals(mtype)
                || MediaType.TEXT_TURTLE.equals(mtype)
                || MediaType.TEXT_URI_LIST.equals(mtype)
                || MediaType.TEXT_VCARD.equals(mtype)
                || MediaType.TEXT_XML.equals(mtype)
                || MediaType.TEXT_YAML.equals(mtype);
    }

    /**
     * 替换sql中用$标识的关键字符号，如：{@code $ -> $$， & -> $&, ' -> $'}<br>
     * example: <br>
     * <p>
     * select * from XXX where name='King's'<br>
     * 转换后应变为：<br>
     * select * from XXX where name='King$'s' escape '$'
     * <p>
     *
     * @param param sql参数
     * @return
     */
    public static String escapeParams(String param) {
        if (param == null || param.isEmpty()) {
            return param;
        }

        param = param.replaceAll("\\$", "\\$\\$");
        param = param.replaceAll("~", "\\$~");
        param = param.replaceAll("!", "\\$!");
        param = param.replaceAll("@", "\\$@");
        param = param.replaceAll("#", "\\$#");
        param = param.replaceAll("%", "\\$%");
        param = param.replaceAll("\\^", "\\$\\^");
        param = param.replaceAll("&", "\\$&");
        param = param.replaceAll("\\*", "\\$\\*");
        param = param.replaceAll("\\(", "\\$\\(");
        param = param.replaceAll("\\)", "\\$\\)");
        param = param.replaceAll("_", "\\$_");
        param = param.replaceAll("\\+", "\\$\\+");
        param = param.replaceAll("\\{", "\\$\\{");
        param = param.replaceAll("\\}", "\\$\\}");
        param = param.replaceAll("\\[", "\\$\\[");
        param = param.replaceAll("\\]", "\\$\\]");
        param = param.replaceAll("\\|", "\\$\\|");
        param = param.replaceAll(";", "\\$;");
        param = param.replaceAll(":", "\\$:");
        param = param.replaceAll("'", "\\$''");
        param = param.replaceAll("\"", "\\$\"");
        param = param.replaceAll(",", "\\$,");
        param = param.replaceAll("<", "\\$<");
        param = param.replaceAll("\\.", "\\$\\.");
        param = param.replaceAll(">", "\\$>");
        param = param.replaceAll("/", "\\$/");
        param = param.replaceAll("\\?", "\\$\\?");
        param = param.replaceAll("\\\\", "\\$\\\\");
        param = param.replaceAll("`", "\\$`");
        param = param.replaceAll("-", "\\$-");
        param = param.replaceAll("=", "\\$=");

        return param;
    }

}
