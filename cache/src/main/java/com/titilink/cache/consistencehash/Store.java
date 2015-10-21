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

/**
 * 存储接口，任何存储实现都要实现该接口来提供存储服务
 *
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public interface Store {

    /**
     * 存储数据到节点
     *
     * @param key 数据key
     * @param obj 数据
     * @return true-存储成功 false-存储失败
     */
    boolean add(String key, Object obj);

    /**
     * 根据数据key删除存储的数据
     *
     * @param key 数据key
     * @return 要删除的数据，如果没有数据key对应的数据，返回null
     */
    Object remove(String key);

    /**
     * 根据数据key更新存储的数据
     *
     * @param key 数据key
     * @param newObj 新的数据
     * @return 被更新的数据，如果没有数据key对应的数据，返回null
     */
    Object update(String key, Object newObj);

    /**
     * 根据数据key查询存储的数据
     *
     * @param key 数据key
     * @return 存储中的数据，如果没有数据key对应的数据，返回null
     */
    Object get(String key);

}
