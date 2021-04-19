package com.example.bloomserver.testPac;

import com.alipay.remoting.rpc.RpcClient;
import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.pojo.PeerNode;
import com.example.bloominterface.pojo.VoteParam;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloomserver.excutor.EnventExcutor;
import com.example.bloomserver.excutor.impl.BloomEnventExcutor;
import com.example.bloomserver.raft.RaftConsistencyModule;
import com.example.bloomserver.raft.RaftLog;
import com.example.bloomserver.raft.RaftNode;
import com.example.bloomserver.rpc.RaftRpcClient;
import com.example.bloomserver.rpc.RaftRpcServer;
import com.example.bloomserver.rpc.RaftUserProcessor;
import com.example.bloomserver.tools.BloomFilter;

import java.util.*;

public class Server5 {
    public static void main(String args[]) {
        String selfId = "127.0.0.1:9905";
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
        RaftLog raftLog = new RaftLog("D:\\raft-log\\test-server\\5");
        RaftNode raftNode = new RaftNode();
        BloomFilter bloomFilter = new BloomFilter(10000, 0.01);
        EnventExcutor enventExcutor = new BloomEnventExcutor(bloomFilter);
        raftNode.init(peerNodeMap, selfId, raftLog,enventExcutor);
        RaftConsistencyModule raftConsistencyModule = new RaftConsistencyModule(raftNode);
        RaftUserProcessor raftUserProcessor = new RaftUserProcessor(raftConsistencyModule);
        RaftRpcServer raftRpcServer = RaftRpcServer.getRaftRpcServer(9905, raftUserProcessor);
//        RaftRpcServer raftRpcServer = new RaftRpcServer();
//        raftRpcServer.init(9905, raftUserProcessor);
//        if(raftRpcServer == null) System.out.println("fail");


//        RaftRpcClient raftRpcClient = new RaftRpcClient();
//        raftRpcClient.init();
//        VoteParam voteParam = new VoteParam(0,selfId,1,1);
//        raftRpcClient.send("127.0.0.1:9901",new RpcRequest(RaftOrderType.ELECTION,voteParam,"127.0.0.1:9901"));

    }
}
