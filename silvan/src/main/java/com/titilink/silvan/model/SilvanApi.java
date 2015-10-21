/**
 * 项目名称: titilink
 * 文件名称: Api.java
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
public class SilvanApi {

    private String uriPrefix;

    private String uri;

    private List<String> versions;

    private String resource;

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SilvanApi{");
        sb.append("uriPrefix='").append(uriPrefix).append('\'');
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", versions=").append(versions);
        sb.append(", resource='").append(resource).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
