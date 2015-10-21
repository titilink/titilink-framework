/**
 * 项目名称: titilink-framework
 * 文件名称: DreactMemTest.java
 * Date: 2015/6/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.framework.jvm.oom;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-06-01
 * since v1.0.0
 */
public class DreactMemTest {

    private static final int _1M = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        Field f = Unsafe.class.getDeclaredFields()[0];
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        while (true) {
            unsafe.allocateMemory(_1M);
        }
    }

}
