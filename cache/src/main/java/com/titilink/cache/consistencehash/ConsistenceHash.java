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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 一致性Hash实现
 *
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
 public class ConsistenceHash {

    /**
     * 获取虚拟节点在一致性hash环上的hash值
     *
     * @param node 实际节点
     * @param iteration 虚拟节点的在组内的序号
     * @return 虚拟节点在一致性hash环上的hash值
     */
    public List<Long> nodePositionsAtIteration(Node node, int iteration) {
        List<Long> positions = new ArrayList<>();
        byte[] digest = computeHashByMd5(getKeyForNode(node, iteration));
        for (int h = 0; h < 4; h++) {
            Long k = ((long) (digest[3 + h * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + h * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + h * 4] & 0xFF) << 8)
                    | (digest[h * 4] & 0xFF);
            positions.add(k);
        }
        return positions;
    }

    /**
     * 获取虚拟节点的key
     *
     * @param node 实际节点
     * @param iteration 虚拟节点的在组内的序号
     * @return 虚拟节点的key
     */
    public String getKeyForNode(Node node, int iteration) {
        return node.ip + "-" + iteration;
    }

    /**
     * 根据MD5算法获取MD5值
     *
     * @param k 需要摘要的字符串
     * @return MD5值
     */
    public byte[] computeHashByMd5(String k) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no md5 algorithm");
        }
        md.update(getKeys(k));
        return md.digest();
    }

    /**
     * 根据MD5算法获取hash值
     *
     * @param k 需要摘要的字符串
     * @return MD5算法获取的hash值
     */
    public long hash(final String k) {
        byte[] bKey = computeHashByMd5(k);
        long rv = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16)
                | ((long) (bKey[1] & 0xFF) << 8)
                | (bKey[0] & 0xFF);
        return rv & 0xffffffffL;
    }

    /**
     * 根据UTF-8编码获取指定字符串的字节数组
     *
     * @param key 指定字符串
     * @return 字节数组
     */
    public byte[] getKeys(final String key) {
        return key.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * 根据数据Key获取要存储的节点
     *
     * @param keyForHash 数据key
     * @param nodeMap 一致性hash环
     * @return 要存储的节点，如果没有找到，抛出RuntimeException
     */
    public Node getNodeToStore(String keyForHash, TreeMap<Long, Node> nodeMap) {
        Long nodeHash = nodeMap.ceilingKey(hash(keyForHash));
        if ( null == nodeHash )
            throw new RuntimeException("no node found to store this message");
        return nodeMap.get(nodeHash);
    }

    /**
     * 构造一致性hash环
     *
     * @param allNodes 所有物理节点
     * @return 一致性hash环
     */
    public TreeMap<Long, Node> buildConsistenceHashRing(List<Node> allNodes) {
        TreeMap<Long, Node> nodeMap = new TreeMap<>();
        int replicator = 100;
        for (Node node : allNodes) {
            for (int i = 0; i < replicator / 4; i++) {
                for(long position : nodePositionsAtIteration(node, i)) {
                    nodeMap.put(position, node);
                }
            }
        }
        return nodeMap;
    }



}
