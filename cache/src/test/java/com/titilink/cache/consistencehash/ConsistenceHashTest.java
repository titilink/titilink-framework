package com.titilink.cache.consistencehash;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 测试存储服务
 *
 * @author kam
 * @date 2015/10/21
 * @since v1.0.0
 */
public class ConsistenceHashTest {

    @Test
    public void testConsistenceHash() {

        ConsistenceHash hash = new ConsistenceHash();

        //构造所有节点
        List<Node> allNodes = new ArrayList<>();
        Node n1 = new Node("127.0.0.1");
        Node n2 = new Node("127.0.0.2");
        Node n3 = new Node("127.0.0.3");
        allNodes.add(n1);
        allNodes.add(n2);
        allNodes.add(n3);

        //构造节点的一致性hash环
        TreeMap<Long, Node> nodeMap = hash.buildConsistenceHashRing(allNodes);

        //存储数据到节点
        String keyForHash = "session-01";
        String data = "this is one message need to store in node.";
        hash.getNodeToStore(keyForHash, nodeMap).add(keyForHash, data);
        String anotherKeyForHash = "session-02";
        String anotherData = "this is another message need to store in node.";
        hash.getNodeToStore(keyForHash, nodeMap).add(anotherKeyForHash, anotherData);

        //从存储节点获取数据
        assert "this is one message need to store in node."
                .equals(hash.getNodeToStore(keyForHash, nodeMap).get(keyForHash));
        assert "this is another message need to store in node."
                .equals(hash.getNodeToStore(keyForHash, nodeMap).get(anotherKeyForHash));
    }

}
