/**
 * Copyright 2005-2015 titilink
 *
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 *
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 *
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 *
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 *
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 *
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 *
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 *
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 *
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.cache.consistencehash;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过内存hashmap来实现存储服务
 *
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public class Node implements Store{

    /**
     * 节点IP
     */
    String ip;

    /**
     * 节点存储的数据
     */
    private ConcurrentHashMap<String, Object> datas = new ConcurrentHashMap<>();

    /**
     * 构造节点
     *
     * @param ip 及诶单IP
     */
    public Node(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean add(String key, Object obj) {
        Object storeObj = datas.put(key, obj);
        return storeObj != null;
    }

    @Override
    public Object remove(String key) {
        return datas.remove(key);
    }

    @Override
    public Object update(String key, Object newObj) {
        return datas.replace(key, newObj);
    }

    @Override
    public Object get(String key) {
        return datas.get(key);
    }

    /**
     * {No Java-doc}
     *
     * @see{Object.toString}
     */
    public String toString() {
        return this.ip;
    }

}
