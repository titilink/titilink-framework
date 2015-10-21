/**
 * 项目名称: titilink-framework
 * 文件名称: ReferenceCountGcTest.java
 * Date: 2015/6/2
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.framework.jvm.gc;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-06-02
 * since v1.0.0
 */
public class ReferenceCountGcTest {

    private Object instance = null;

    private static final int _1M = 1024 * 1024;

    private byte[] bigSize = new byte[_1M];

    public static void main(String[] args) {
        ReferenceCountGcTest a = new ReferenceCountGcTest();
        ReferenceCountGcTest b = new ReferenceCountGcTest();
        a.instance = b;
        b.instance = a;

        a = null;
        b = null;

        System.gc();
    }

}