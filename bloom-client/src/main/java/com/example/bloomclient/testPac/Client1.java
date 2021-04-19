package com.example.bloomclient.testPac;

import com.example.bloomclient.ThreadPool.RaftThread;
import com.example.bloomclient.ThreadPool.RaftThreadPool;
import com.example.bloomclient.client.BloomClient;
import com.example.bloominterface.pojo.PeerNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client1 {


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
        peerNodeMap.put(peerNode5.getNodeId(), peerNode5);
        RaftThreadPool raftThreadPool = new RaftThreadPool();
        BloomClient bloomClient = new BloomClient(peerNodeMap, "127.0.0.1:9902", raftThreadPool);
        bloomClient.init();
        Scanner scanner = new Scanner(System.in);
        for (; ; ) {
//            String value = scanner.next();
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (bloomClient.write(new Random(123) + "zhjyhfawhfaw")) {
                    System.out.println("写入成功");
                } else {
                    System.out.println("写入失败");
                }
            } catch (Exception e) {
                System.out.println("超时错误");
            }
        }

    }


}
