/**
 * 项目名称: titilink-framework
 * 文件名称: SOFTest.java
 * Date: 2015/6/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.framework.jvm.oom;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-06-01
 * since v1.0.0
 */
public class SOFTest {

    private static int stackLength = 0;

    private static void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        try {
            stackLeak();
        } catch (Throwable e) {
            System.out.print("statck length = " + stackLength);
            throw e;
        }
    }

}
