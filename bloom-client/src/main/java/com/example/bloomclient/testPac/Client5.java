package com.example.bloomclient.testPac;

import com.example.bloomclient.ThreadPool.RaftThreadPool;
import com.example.bloomclient.client.BloomClient;
import com.example.bloominterface.pojo.PeerNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client5 {
    public static void main(String args[]) {


        Map<String, PeerNode> peerNodeMap = new HashMap<>();

        PeerNode peerNode1 = new PeerNode("127.0.0.1:9901", "127.0.0.1:9901");
        peerNodeMap.put(peerNode1.getNodeId(), peerNode1);
        PeerNode peerNode2 = new PeerNode("127.0.0.1:9902", "127.0.0.1:9902");
        peerNodeMap.put(peerNode2.getNodeId(), peerNode2);
        PeerNode peerNode3 = new PeerNode("127.0.0.1:9903", "127.0.0.1:9903");
        peerNodeMap.put(peerNode3.getNodeId(), peerNode3);
        PeerNode peerNode4 = new PeerNode("127.0.0.1:9904", "127.0.0.1:9904");
        peerNodeMap.put(peerNode4.getNodeId(), peerNode4);
        PeerNode peerNode5 = new PeerNode("127.0.0.1:9905", "127.0.0.1:9905");
        RaftThreadPool raftThreadPool = new RaftThreadPool();
        BloomClient bloomClient = new BloomClient(peerNodeMap, null, raftThreadPool);
        bloomClient.init();
        Scanner scanner = new Scanner(System.in);
        for (; ; ) {
            String expectedQuantity = scanner.next();
            System.out.println(bloomClient.getLeaderId());
        }

    }

}
