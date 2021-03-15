package com.example.bloomserver.raft;


import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.pojo.LogEntry;
import com.example.bloominterface.pojo.PeerNode;
import com.example.bloominterface.pojo.VoteParam;
import com.example.bloominterface.pojo.VoteRes;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import com.example.bloomserver.ThreadPool.RaftThreadPool;
import com.example.bloomserver.rpc.RaftRpcClient;
import org.rocksdb.RocksDBException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RaftNode {


    //选举超时时间基数
    private volatile long electionTimeout = 500;

    //随机选举超时时间,为150-300之间的随机数,单位为毫秒
    private volatile long randomElectionTimeout;


    //心跳超时时间
    private volatile long heartbeatTimeout;


    //心跳间隔时间,单位为毫秒
    private volatile long heartbeatInterval;

    //节点上次接收到心跳的时间戳,单位为毫秒
    private volatile long lastHeartBeatTime;


    //当前任期
    private volatile long currentTerm;

    //当前任期选举的候选人
    private volatile String votedFor;

    //已知已经提交的最高日志索引
    private volatile long commitIndex;

    //已经被应用到状态机的最高的日志条目的索引（初始值为0，单调递增）
    private volatile long lastApplied;

    //raft日志
    private RaftLog logMachine;

//    //raft状态机
//    private RaftLog stateMachine;

    //同伴节点的集合
    private Map<String, PeerNode> peerMap;

    //判断当前节点角色状态
    private volatile int status;


    //判断当前节点Id
    private String selfId;

    //任期锁
    private ReentrantLock termLock = new ReentrantLock();

    //rpc客户端,用于和其他节点通信
    private RaftRpcClient raftRpcClient;

    private RaftThreadPool raftThreadPool;


    public void init() {
        ExecutorService raftThreadPool = Executors.newScheduledThreadPool(2);

    }


    public void resetRandomElectionTimeout() {
        this.randomElectionTimeout = this.electionTimeout + ThreadLocalRandom.current().nextLong(300);
    }


    public VoteRes handleVoteRequest() {
        return null;
    }


    class LeaderSurvivalCheckHeartbeat implements Runnable {

        @Override
        public void run() {
            try {
                termLock.tryLock(300, TimeUnit.MILLISECONDS);
                if (status == NodeStatus.leader) {
                    return;
                }
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastHeartBeatTime > randomElectionTimeout) {
                    status = NodeStatus.candidate;
                    resetRandomElectionTimeout();
                }
                long lastIndex = logMachine.getLastIndex();
                LogEntry logEntry = logMachine.getLog(lastIndex);
                ++currentTerm;
                VoteParam voteParam = null;
                if (logEntry == null) {
                    voteParam = new VoteParam(currentTerm, selfId, lastIndex, currentTerm);
                } else {
                    voteParam = new VoteParam(currentTerm, selfId, logEntry.getIndex(), logEntry.getTerm());
                }
                CountDownLatch countDownLatch = new CountDownLatch(peerMap.size() - 1);
                AtomicInteger successCount = new AtomicInteger(0);
                for (String nodeId : peerMap.keySet()) {
                    if (!nodeId.equals(selfId)) {
                        PeerNode peerNode = peerMap.get(nodeId);
                        RpcRequest rpcRequest = new RpcRequest(RaftOrderType.ELECTION, voteParam, peerNode.getAddress());
                        raftThreadPool.submit(new Callable() {
                            @Override
                            public Object call() throws Exception {
                                RpcResponse<VoteRes> rpcResponse = raftRpcClient.send(peerNode.getAddress(), rpcRequest);
                                if(rpcResponse == null){

                                }

                            }
                        });

                    }
                }


            } catch (InterruptedException | RocksDBException e) {
                e.printStackTrace();
            } finally {
                termLock.unlock();
            }
        }
    }


}
