/**
 * 项目名称: titilink-framework
 * 文件名称: ConsistenceHash.java
 * Date: 2015/8/11
 * Copyright: 2015 www.titilink.com Inc. All rights reserved.
 * 注意：本内容仅限于titilink公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.titilink.cache.consistencehash

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性Hash实现
 *
 * @author ganting
 * @since v1.0.0
 */
 public class ConsistenceHash {

    /**
     * 存储
     */
    private interface Store {

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

    /**
     * 存储节点
     */
    private class Node implements Store {

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

    /**
     * 获取虚拟节点在一致性hash环上的hash值
     *
     * @param node 实际节点
     * @param iteration 虚拟节点的在组内的序号
     * @return 虚拟节点在一致性hash环上的hash值
     */
    private List<Long> nodePositionsAtIteration(Node node, int iteration) {
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
    private String getKeyForNode(Node node, int iteration) {
        return node.ip + "-" + iteration;
    }

    /**
     * 根据MD5算法获取MD5值
     *
     * @param k 需要摘要的字符串
     * @return MD5值
     */
    private byte[] computeHashByMd5(String k) {
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
    private long hash(final String k) {
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
    private byte[] getKeys(final String key) {
        return key.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * 根据数据Key获取要存储的节点
     *
     * @param keyForHash 数据key
     * @param nodeMap 一致性hash环
     * @return 要存储的节点，如果没有找到，抛出RuntimeException
     */
    private Node getNodeToStore(String keyForHash, TreeMap<Long, Node> nodeMap) {
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
    private TreeMap<Long, Node> buildConsistenceHashRing(List<Node> allNodes) {
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

    @Test
    public void testConsistenceHash() {
        //构造所有节点
        List<Node> allNodes = new ArrayList<>();
        Node n1 = new Node("127.0.0.1");
        Node n2 = new Node("127.0.0.2");
        Node n3 = new Node("127.0.0.3");
        allNodes.add(n1);
        allNodes.add(n2);
        allNodes.add(n3);

        //构造节点的一致性hash环
        TreeMap<Long, Node> nodeMap = buildConsistenceHashRing(allNodes);

        //存储数据到节点
        String keyForHash = "session-01";
        String data = "this is one message need to store in node.";
        getNodeToStore(keyForHash, nodeMap).add(keyForHash, data);
        String anotherKeyForHash = "session-02";
        String anotherData = "this is another message need to store in node.";
        getNodeToStore(keyForHash, nodeMap).add(anotherKeyForHash, anotherData);

        //从存储节点获取数据
        assert "this is one message need to store in node."
                .equals(getNodeToStore(keyForHash, nodeMap).get(keyForHash));
        assert "this is another message need to store in node."
                .equals(getNodeToStore(keyForHash, nodeMap).get(anotherKeyForHash));
    }

}
