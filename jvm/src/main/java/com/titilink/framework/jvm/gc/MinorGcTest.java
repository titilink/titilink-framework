/**
 * 项目名称: titilink-framework
 * 文件名称: MinorGcTest.java
 * Date: 2015/6/3
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.framework.jvm.gc;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-06-03
 * since v1.0.0
 */
public class MinorGcTest {

    private static final int _2M = 2 * 1024 * 1024;

    private static final int _4M = 2 * _2M;

    public static void main(String[] args) {
        byte[] associate1, associate2, associate3, associate4;
        associate1 = new byte[_2M];
        associate2 = new byte[_2M];
        associate3 = new byte[_2M];

        //System.gc();

        associate4 = new byte[_4M];

    }

}
