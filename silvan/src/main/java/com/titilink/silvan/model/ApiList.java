/**
 * 项目名称: titilink
 * 文件名称: ApiList.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.silvan.model;

import java.util.List;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class ApiList {

    private List<SilvanApi> apis;

    public List<SilvanApi> getApis() {
        return apis;
    }

    public void setApis(List<SilvanApi> apis) {
        this.apis = apis;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ApiList{");
        sb.append("apis=").append(apis);
        sb.append('}');
        return sb.toString();
    }
}
