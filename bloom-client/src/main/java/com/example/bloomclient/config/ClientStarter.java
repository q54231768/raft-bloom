package com.example.bloomclient.config;

import com.example.bloomclient.ThreadPool.RaftThreadPool;
import com.example.bloomclient.client.BloomClient;
import com.example.bloominterface.pojo.PeerNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClientStarter {

    @Bean
    public BloomClient bloomClient(@Value("${BloomServer.node.list}") String nodeListStr) {
        nodeListStr = nodeListStr.replaceAll(" ","");
        String nodeAddressList[] = nodeListStr.split(";");
        Map<String, PeerNode> peerNodeMap = new HashMap<>();
        for (int i = 0; i < nodeAddressList.length; ++i) {
            PeerNode peerNode = new PeerNode(nodeAddressList[i],nodeAddressList[i]);
            peerNodeMap.put(nodeAddressList[i],peerNode);
        }
        RaftThreadPool raftThreadPool = new RaftThreadPool();
        BloomClient bloomClient = new BloomClient(peerNodeMap, null, raftThreadPool);
        bloomClient.init();
        return bloomClient;
    }


}
