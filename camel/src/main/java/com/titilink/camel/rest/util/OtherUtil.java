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

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * <p>
 * @author by kam
 * @date 2015/05/01
 * @since v1.0.0
 */
public final class OtherUtil {

    private static Random rand = new Random(OtherUtil.getCurrentTime());

    private static String localHostname = "";

    private static final int NANO_TO_MILL = 1000000;

    /**
     * 十进制255
     */
    private static final int HEX_255 = 0xFF;

    /**
     * 十进制2
     */
    private static final int DECIMAL_2 = 2;

    /**
     * 十进制16
     */
    private static final int DECIMAL_16 = 16;


    /**
     * 删除文件名中的路径，只返回文件名
     *
     * @param filename --含完整路径的文件名
     * @return String  --不含路径的文件名
     */
    public static String removePath(String filename) {
        int i = filename.lastIndexOf('/');
        if (i == -1) {
            return filename;
        } else {
            return filename.substring(i + 1);
        }
    }

    /**
     * 获取文件最后一次被修改的时间
     *
     * @param filename --文件名
     * @return long -- 0-表示文件不存在或无权限读取，大于0为修改时间
     */
    public static long getFileLastModified(String filename) {
        long l = 0;
        File f = new File(filename);
        if (f.exists()) {
            try {
                l = f.lastModified();
            } catch (SecurityException se) {
                l = 0;
                se.printStackTrace(); //NOPMD
            }
        }
        return l;
    }

    /**
     * 获取本机名称
     *
     * @return String --本机名称
     */
    public static String getLocalHostName() {
        if (localHostname.equals("")) {
            try {
                localHostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                localHostname = "UnknownHost";
            }
        }
        return localHostname;
    }

    /**
     * 获取本地所有的ip地址，组成字符串返回（以char=2分隔）
     *
     * @return String --本地所有的ip地址的字符串表示
     */
    public static String getLocalHostIps() {
        StringBuffer sb = new StringBuffer();
        final char flag = 2;
        try {
            Enumeration<?> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    String ip = inetAddress.getHostAddress();
                    if (!inetAddress.isLoopbackAddress() && ip.indexOf(":") == -1) {
                        sb.append(ip).append(flag);
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return sb.toString();
    }

    /**
     * 获取随机数
     *
     * @return Random --随机数对象
     */
    public static Random getRandom() {
        return rand;
        // return new Random(OtherUtil.getCurrentTime());
    }

    /**
     * Generates pseudo-random long from specific range. Generated number is
     * great or equals to min parameter value and less then max parameter value.
     *
     * @param min lower (inclusive) boundary
     * @param max higher (exclusive) boundary
     * @return pseudo-random value
     */

    public static long randomLong(long min, long max) {
        return min + (long) (Math.random() * (max - min));
    }

    /**
     * 清空数组
     *
     * @param arg --对象数组
     */
    public static void clearArray(Object arg[]) {
        if (arg == null) {
            return;
        }
        for (int i = 0; i < arg.length; i++) {
            arg[i] = null;
        }
        // arg = null;
    }

    /**
     * Generates pseudo-random integer from specific range. Generated number is
     * great or equals to min parameter value and less then max parameter value.
     *
     * @param min lower (inclusive) boundary
     * @param max higher (exclusive) boundary
     * @return pseudo-random value
     */
    public static int randomInt(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    /**
     * 中断线程一段时间，可代替sleep，采取object.wait实现
     *
     * @param lockObj  加锁对象
     * @param sometime ，单位毫秒
     * @throws InterruptedException --阻塞被中断时抛出异常
     */
    public static void blockSomeTime(final Object lockObj, long sometime) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        synchronized (lockObj) {
            long waitTime = sometime;
            long start = OtherUtil.getCurrentTime();
            try {
                for (; ; ) {
                    if (waitTime > 0) {
                        lockObj.wait(waitTime);
                    } else {
                        break;
                    }

                    waitTime = sometime - (OtherUtil.getCurrentTime() - start);
                }
            } catch (InterruptedException ex) {
                lockObj.notifyAll();
                throw ex;
            }
        }
    }

    /**
     * 对字符串进行BASE64编码
     *
     * @param str --源字符串
     * @return --编码后的字符串
     */
    public static String strBase64Encode(String str) {
        Base64 b64 = new Base64();
        byte[] b = b64.encode(str.getBytes());
        return new String(b);
    }

    /**
     * 对字符串做BASE64解码
     *
     * @param base64EncodeStr --经过BASE64编码的字符串
     * @return String          --解码后的字符串
     */
    public static String strBase64Decode(String base64EncodeStr) {
        Base64 b64 = new Base64();
        byte[] b = b64.decode(base64EncodeStr.getBytes());
        return new String(b);
    }

    /**
     * 对字符串做MD5处理后再做BASE64编码
     *
     * @param str --待处理的源字符串
     * @return --编码后的字符串
     */
    public static String md5AndBase64Encode(String str) {
        byte[] b = md5(str);
        if (b == null) {
            throw new RuntimeException("md5 encode err!");
        }
        Base64 b64 = new Base64();
        b = b64.encode(b);
        return new String(b);
    }

    /**
     * 对字符串做MD5散列处理
     *
     * @param str --待处理的源字符串
     * @return --处理后的字符串
     */
    public static byte[] md5(String str) {

        java.security.MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
            return digest.digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 打印字符串
     *
     * @param info --待打印字符串
     */
    public static void systemOut(String info) {
        //规避静态检查
        PrintStream ps = System.out;
        ps.println(info);
    }


    /**
     * 触发虚拟机垃圾回收
     */
    public static void jvmGC() {
        //规避静态检查
        Runtime rt = Runtime.getRuntime();
        rt.gc();
    }

    /**
     * 空锁
     *
     * @param o --待锁对象
     */
    public static void emptyBlock(Object o) {
        //规避静态检查
        ; //NOPMD
    }

    /**
     * 检查ip地址的合法性
     *
     * @param ip --待检查IP地址
     * @return boolean  --地址合法返回true，否则返回false
     */
    public static boolean checkip(String ip) {
        Pattern patt = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        Matcher mat = patt.matcher(ip);
        return mat.matches();
    }

    /**
     * 获取一个dns或计算机名称所对应的ip地址数组
     *
     * @param dnsip --DNS或计算机名称
     * @return String[] --IP地址数组
     */
    public static String[] getDnsIPs(String dnsip) {
        String ips[];
        try {
            InetAddress ias[] = InetAddress.getAllByName(dnsip);
            ips = new String[ias.length];
            for (int i = 0; i < ias.length; i++) {
                ips[i] = ias[i].getHostAddress();
                ias[i] = null;
            }
        } catch (UnknownHostException e) {
            ips = null;
        }
        return ips;
    }

    /**
     * 获取当前的物理毫秒时间
     *
     * @return long --当前时间
     */
    public static long getCurrentTime() {
        return System.nanoTime() / NANO_TO_MILL;
    }

    /**
     * 将二进制转换十六进制
     *
     * @param buf 字节数组
     * @return 字符串
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & HEX_255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase(Locale.US));
        }
        return sb.toString();

    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 字符串
     * @return 字节数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return new byte[0];
        }
        byte[] result = new byte[hexStr.length() / DECIMAL_2];
        for (int i = 0; i < hexStr.length() / DECIMAL_2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * DECIMAL_2, i * DECIMAL_2 + 1), DECIMAL_16);
            int low = Integer.parseInt(hexStr.substring(i * DECIMAL_2 + 1, i * DECIMAL_2 + DECIMAL_2), DECIMAL_16);
            result[i] = (byte) (high * DECIMAL_16 + low);
        }
        return result;
    }

}
