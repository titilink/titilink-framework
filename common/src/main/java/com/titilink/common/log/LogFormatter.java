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
package com.titilink.common.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志格式转化器，日志格式默认为：
 * <p>
 * @author by kam
 * @date 2015/04/29
 * @since v1.0.0
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
