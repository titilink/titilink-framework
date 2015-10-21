/**
 * 项目名称: titilink
 * 文件名称: LogFormatter.java
 * Date: 2015/4/29
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.common.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志格式转化器，日志格式默认为：
 * <p>
 * author by ganting
 * date 2015-04-29
 * since v1.0.0
 */
public final class LogFormatter {

    private static final int NUM_50 = 50;
    public static final int NUM_2 = 2;

    public static String format(String paramString, Object paramObject) {
        return arrayFormat(paramString, new Object[]{paramObject});
    }

    public static String arrayFormat(String paramString, Object[] paramArr) {
        if (null == paramString) {
            return null;
        }
        if (null == paramArr) {
            return paramString;
        }
        int i = 0;
        StringBuffer sb = new StringBuffer(paramString.length() + NUM_50);
        for (int h = 0, len = paramArr.length; h < len; h++) {
            int j = paramString.indexOf("{}", i);
            if (j == -1) {
                if (i == 0) {
                    return paramString;
                }
                sb.append(paramString.substring(i, paramString.length()));
                return sb.toString();
            }
            if (isEscapedDelimeter(paramString, j)) {
                if (!isDoubleEscaped(paramString, j)) {
                    h--;
                    sb.append(paramString.substring(i, j - 1));
                    sb.append('{');
                    i = j + 1;
                } else {
                    sb.append(paramString.substring(i, j - 1));
                    deeplyAppendParameter(sb, paramArr[h], new HashMap<Object[], Object>());
                    i = j + NUM_2;
                }
            } else {
                sb.append(paramString.substring(i, j - 1));
                deeplyAppendParameter(sb, paramArr[h], new HashMap<Object[], Object>());
                i = j + NUM_2;
            }
        }
        return null;
    }

    private static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == '\\';
    }

    private static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return (delimeterStartIndex >= NUM_2) && (messagePattern.charAt(delimeterStartIndex - NUM_2) == '\\');
    }

    private static void deeplyAppendParameter(StringBuffer sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            sbuf.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(sbuf, o);
        } else if (o instanceof boolean[]) {
            booleanArrayAppend(sbuf, (boolean[]) (boolean[]) o);
        } else if (o instanceof byte[]) {
            byteArrayAppend(sbuf, (byte[]) (byte[]) o);
        } else if (o instanceof char[]) {
            charArrayAppend(sbuf, (char[]) (char[]) o);
        } else if (o instanceof short[]) {
            shortArrayAppend(sbuf, (short[]) (short[]) o);
        } else if (o instanceof int[]) {
            intArrayAppend(sbuf, (int[]) (int[]) o);
        } else if (o instanceof long[]) {
            longArrayAppend(sbuf, (long[]) (long[]) o);
        } else if (o instanceof float[]) {
            floatArrayAppend(sbuf, (float[]) (float[]) o);
        } else if (o instanceof double[]) {
            doubleArrayAppend(sbuf, (double[]) (double[]) o);
        } else {
            objectArrayAppend(sbuf, (Object[]) (Object[]) o, seenMap);
        }
    }

    private static void safeObjectAppend(StringBuffer sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable t) //NOPMD
        {
            t.printStackTrace(); //NOPMD
            sbuf.append("[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuffer sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1) {
                    sbuf.append(", ");
                }
            }
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuffer sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuffer sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuffer sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuffer sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuffer sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuffer sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuffer sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuffer sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

}
