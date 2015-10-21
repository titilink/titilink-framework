/**
 * 项目名称: titilink
 * 文件名称: PluginableApplication.java
 * Date: 2015/5/1
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.camel.rest.server;

import com.titilink.camel.rest.common.AdapterRestletUtil;
import com.titilink.camel.rest.common.ApplicationPlugin;
import com.titilink.common.log.AppLogger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：
 * <p>
 * author by ganting
 * date 2015-05-01
 * since v1.0.0
 */
public class PluginableApplication extends Application {

    private static final AppLogger LOGGER = AppLogger.getInstance(PluginableApplication.class);

    /**
     * uri前缀只允许小写字母与数字，由斜线开头，非斜线或星号结尾，可以存在小数点
     */
    private Pattern pattern = Pattern.compile("^(/[a-z\\d\\.]*)+[^/*]$");

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        LOGGER.debug("createInboundRoot begin.");
        Router root = new Router(getContext());
        Router router = new Router(getContext());

        // 添加uri前缀，如/rest/1.0/arcontrol
        String uriPattern = getUriPrefix(AdapterRestletUtil.getUriPattern());

        if (uriPattern == null || uriPattern.trim().isEmpty()) {
            root.attach(router);
        } else {
            root.attach(uriPattern, router);
        }

        ApplicationPlugin[] plugins = AdapterRestletUtil.getPlugins();
        if (null == plugins) {
            return root;
        }

        // 将插件定义的路由attach到系统的router上
        for (ApplicationPlugin plugin : plugins) {
            if (plugin != null) {
                LOGGER.debug("createInboundRoot plugin={}", plugins.getClass().getCanonicalName());
                plugin.attachTo(router);
            }
        }

        LOGGER.debug("createInboundRoot end, plugins.length={}", plugins.length);
        return root;
    }

    private String getUriPrefix(String uriPattern) {
        if (uriPattern == null || uriPattern.trim().isEmpty()) {
            return null;
        }
        uriPattern = Normalizer.normalize(uriPattern, Normalizer.Form.NFKC);
        Matcher m = pattern.matcher(uriPattern);
        boolean b = m.matches();
        if (!b) {
            return null;
        }

        return uriPattern;
    }

}
