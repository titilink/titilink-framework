package com.titilink.silvan.auth;

import com.titilink.common.log.AppLogger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.routing.Filter;

/**
 * 描述：[描述]
 *
 * @author kam
 * @date 2015/10/24
 * @since [版本号]
 */
public class AuthenticationFilter extends Filter {

    private static final AppLogger LOGGER = AppLogger.getInstance(AuthenticationFilter.class);

    @Override
    protected int beforeHandle(Request request, Response response) {
        LOGGER.debug("filter api request with authentication");

        return super.beforeHandle(request, response);
    }
}
