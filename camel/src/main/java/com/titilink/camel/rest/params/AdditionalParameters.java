/**
 * 项目名称: titilink
 * 文件名称: AdditionalParameters.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.params;

import org.restlet.data.ChallengeResponse;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class AdditionalParameters {

    private ChallengeResponse challengeResponse = null;

    /**
     * 获取 ChallengeResponse
     *
     * @return ChallengeResponse
     */
    public ChallengeResponse getChallengeResponse() {
        return challengeResponse;
    }

    /**
     * 设置 ChallengeResponse<br>
     * Authentication response sent by client to an origin server.
     * This is typically following a ChallengeRequest sent by the origin server to the client.
     *
     * @param challengeResponse ChallengeResponse
     */
    public void setChallengeResponse(ChallengeResponse challengeResponse) {
        this.challengeResponse = challengeResponse;
    }

}
