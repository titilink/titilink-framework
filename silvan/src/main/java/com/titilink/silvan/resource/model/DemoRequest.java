/**
 * Copyright 2005-2015 titilink
 * <p/>
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * <p/>
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * <p/>
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * <p/>
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * <p/>
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * <p/>
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * <p/>
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * <p/>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 * <p/>
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.silvan.resource.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.titilink.camel.rest.util.CommonCode;
import com.titilink.camel.rest.util.ValidationUtil;

import javax.validation.constraints.NotNull;

/**
 * 描述：[描述]
 * @author kam
 * @date 2015/10/22
 * @since [版本号]
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DemoRequest {

    @NotNull(message= CommonCode.INVALID_INPUT_PARAMETER + ValidationUtil.MESSAGE_TEMPLATE_SEPARATOR +
         "can not be null")
    @JsonProperty("name")
    private String name;

    @NotNull(message= CommonCode.INVALID_INPUT_PARAMETER + ValidationUtil.MESSAGE_TEMPLATE_SEPARATOR +
        "can not be null")
    @JsonProperty("age")
    private int age;

    @NotNull(message= CommonCode.INVALID_INPUT_PARAMETER + ValidationUtil.MESSAGE_TEMPLATE_SEPARATOR +
        "can not be null")
    @JsonProperty("sex")
    private boolean sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DemoRequest{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", sex=").append(sex);
        sb.append('}');
        return sb.toString();
    }
}
