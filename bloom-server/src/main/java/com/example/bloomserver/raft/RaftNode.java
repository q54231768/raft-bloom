package com.example.bloomserver.raft;


import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.common.ResultCode;
import com.example.bloominterface.exception.GlobalLoggerHandler;
import com.example.bloominterface.pojo.*;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import com.example.bloomserver.ThreadPool.RaftThreadPool;
import com.example.bloomserver.excutor.EnventExcutor;
import com.example.bloomserver.rpc.RaftRpcClient;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RaftNode {


    //选举超时时间基数,单位为毫秒
    private volatile long electionTime = 5000;

    //随机选举超时时间,electionTime与0-300之间的随机数的和,单位为毫秒
    private volatile long randomElectionTimeout;


    //心跳超时时间
//    private volatile long heartbeatTimeout;


    //心跳间隔时间,单位为毫秒
    private volatile long heartbeatInterval = 1000;

    //节点上次接收到心跳的时间戳,单位为毫秒,初始为0
    private volatile long lastHeartBeatTime = 0;

    //选举超时检查间隔
    private volatile long electionTimeoutCheckInterval = 100;


    //当前任期
    private volatile long currentTerm = 0;

    //当前任期选举的候选人
    private volatile String votedFor = null;

    //已知已经提交的最高日志索引
    private volatile long commitIndex = -1;

    //已经被应用到状态机的最高的日志条目的索引（初始值为0，单调递增）
    private volatile long lastApplied = -1;

    //raft日志
    private RaftLog logMachine;

    //同伴节点的集合
    private Map<String, PeerNode> peerMap;

    //判断当前节点角色状态
    private volatile int status = NodeStatus.follower;


    //当前节点Id
    private String selfId;

    //当前系统领导人Id
    private String leaderId = null;


    //任期锁
    private ReentrantLock termLock = new ReentrantLock();

    //节点状态锁
    private ReentrantLock statusLock = new ReentrantLock();


    //选举投票锁
    private ReentrantLock voteRock = new ReentrantLock();

    //日志复制锁
    private ReentrantLock replicationLock = new ReentrantLock();

    //rpc客户端,用于和其他节点通信
    private RaftRpcClient raftRpcClient;

    //任务线程池
    private RaftThreadPool raftThreadPool;

    //事件处理器,已提交日志的实际执行者
    private EnventExcutor enventExcutor;


    //    private static final Logger LOGGER = LoggerFactory.getLogger(RaftNode.class);
    private static final Logger LOGGER = null;


    public void init(Map<String, PeerNode> peerNodeMap,
                     String selfId,
                     RaftLog logMachine,
                     EnventExcutor enventExcutor) {
        try {
            this.peerMap = peerNodeMap;
            this.selfId = selfId;
            this.logMachine = logMachine;
            long lastIndex = logMachine.getLastIndex();
            LogEntry logEntry = logMachine.getLog(lastIndex);
            this.enventExcutor = enventExcutor;
            this.currentTerm = -1;
            if (logEntry != null) {
                this.currentTerm = logEntry.getTerm();
            }
            raftRpcClient = new RaftRpcClient();
            raftRpcClient.init();
            resetRandomElectionTimeout();
            this.raftThreadPool = new RaftThreadPool();
            raftThreadPool.init();
            raftThreadPool.scheduleAtFixedRate(new HeartBeatTask(), 5000, heartbeatInterval);
            raftThreadPool.scheduleWithFixedRate(new ElectionTimeOutCheckTask(), 5000, electionTimeoutCheckInterval);
        } catch (RocksDBException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        }
    }


    public void resetRandomElectionTimeout() {
        this.randomElectionTimeout = this.electionTime + ThreadLocalRandom.current().nextLong(10) * 100;
    }


    public void incrementTerm() throws InterruptedException {
        try {
            termLock.tryLock(300, TimeUnit.MILLISECONDS);
            ++currentTerm;
        } catch (InterruptedException e) {
            throw e;
        } finally {
            termLock.unlock();
        }
    }

    public boolean setCurrentTerm(long term) throws InterruptedException {
        try {
            termLock.tryLock(300, TimeUnit.MILLISECONDS);
            if (currentTerm <= term) {
                currentTerm = term;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            termLock.unlock();
        }
        return true;
    }


    public long getCurrentTerm() {
        return currentTerm;
    }


    public void updateLastHeartBeatTime() {
        lastHeartBeatTime = System.currentTimeMillis();
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String nodeId) {
        leaderId = nodeId;
    }

    public RaftRpcClient getRaftRpcClient() {
        return raftRpcClient;
    }

    public void setRaftRpcClient(RaftRpcClient raftRpcClient) {
        this.raftRpcClient = raftRpcClient;
    }

    public long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(long lastHeartBeatTime) {
        if (lastHeartBeatTime > this.lastHeartBeatTime) this.lastHeartBeatTime = lastHeartBeatTime;
    }

    public boolean setStatus(int newStatus) throws InterruptedException {
        boolean result = false;
        try {
            statusLock.tryLock(300, TimeUnit.MILLISECONDS);
            if (newStatus == NodeStatus.leader) {
                if (status == NodeStatus.candidate) {
                    status = newStatus;
                    result = true;
                }
            } else {
                status = newStatus;
                result = true;
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            statusLock.unlock();
        }
        return true;
    }


    public int getStatus() {
        return status;
    }

    public void setVotedFor(String nodeId) {
        votedFor = nodeId;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public ReentrantLock getVoteRock() {
        return voteRock;
    }

    public void setVoteRock(ReentrantLock voteRock) {
        this.voteRock = voteRock;
    }

    public ReentrantLock getReplicationLock() {
        return replicationLock;
    }

    public void setReplicationLock(ReentrantLock replicationLock) {
        this.replicationLock = replicationLock;
    }

    public RaftLog getLogMachine() {
        return logMachine;
    }

    public void setLogMachine(RaftLog logMachine) {
        this.logMachine = logMachine;
    }


    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public long getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(long lastApplied) {
        this.lastApplied = lastApplied;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public Map<String, PeerNode> getPeerMap() {
        return peerMap;
    }


    public void setPeerMap(Map<String, PeerNode> peerMap) {
        this.peerMap = peerMap;
    }

    public RaftThreadPool getRaftThreadPool() {
        return raftThreadPool;
    }

    public void setRaftThreadPool(RaftThreadPool raftThreadPool) {
        this.raftThreadPool = raftThreadPool;
    }

    public EnventExcutor getEnventExcutor() {
        return enventExcutor;
    }

    public void setEnventExcutor(EnventExcutor enventExcutor) {
        this.enventExcutor = enventExcutor;
    }

    public void submitLogReplicationRequestTask(PeerNode peerNode,
                                                RpcRequest rpcRequest,
                                                CountDownLatch countDownLatch,
                                                AtomicInteger successCount) {

        raftThreadPool.execute(new LogReplicationRequestTask(peerNode, rpcRequest, countDownLatch, successCount));
    }


    //将当前应用到物理状态机日志位置的下一位到index位的日志内容执行
    public void writeData(long index) throws RocksDBException {
        for (long i = lastApplied + 1; i <= index; ++i) {
            enventExcutor.writeIn(logMachine.getLog(i));
        }
    }

    public ClientCommandRes readData(Object content){
        return enventExcutor.read(content);
    }

    public ClientCommandRes getServerConfig(Object paramter){
        return enventExcutor.getServerDeatil(paramter);
    }


    public void beReadyToBeALeader() throws RocksDBException {
        for (String nodeId : peerMap.keySet()) {
            PeerNode peerNode = peerMap.get(nodeId);
            peerNode.setNextIndexAndMatchIndex(logMachine.getLastIndex() + 1, -1);
        }
    }


    public boolean checkElectionTimeOut() {
        try {
            voteRock.tryLock(300, TimeUnit.MILLISECONDS);
            if (getStatus() == NodeStatus.leader) {
                return false;
            }
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastHeartBeatTime > randomElectionTimeout) {
                setStatus(NodeStatus.candidate);
                resetRandomElectionTimeout();
                updateLastHeartBeatTime();
            } else {
                return false;
            }
            setVotedFor(selfId);
            setLeaderId(null);
            incrementTerm();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            voteRock.unlock();
        }
        return true;
    }

    public void sendLogToAllPeer() {
        try {
            replicationLock.tryLock(2000, TimeUnit.MILLISECONDS);
            CountDownLatch countDownLatch = new CountDownLatch(peerMap.size() - 1);
            AtomicInteger successCount = new AtomicInteger(peerMap.size() - 1);
            long lastIndex = logMachine.getLastIndex();
            for (String nodeId : peerMap.keySet()) {
                if (nodeId.equals(selfId)) continue;
                PeerNode peerNode = peerMap.get(nodeId);
                long nextIndex = peerNode.getNextIndex();
                int vacancy = (int) (lastIndex - nextIndex + 1);
                if (vacancy > 500) {
                    vacancy = 500;
                }
                LogEntry[] logEntrys = new LogEntry[vacancy];
                for (int i = 0; i < vacancy; ++i) {
                    logEntrys[i] = logMachine.getLog(nextIndex + i);
                }
                long prevIndex = nextIndex - 1;
                long prevTeam = -1;
                LogEntry prevLog = logMachine.getLog(prevIndex);
                if (prevLog != null) {
                    prevTeam = prevLog.getTerm();
                }
                AddEntryParam addEntryParam = new AddEntryParam(currentTerm,
                        selfId,
                        prevIndex,
                        prevTeam,
                        logEntrys,
                        commitIndex);
                RpcRequest req = new RpcRequest(RaftOrderType.LOG_REPLICATION, addEntryParam, peerNode.getAddress());
                submitLogReplicationRequestTask(peerNode, req, countDownLatch, successCount);
            }
            countDownLatch.await(600, TimeUnit.MILLISECONDS);
            long indexs[] = new long[peerMap.size()];
            int pos = 0;
            for (String nodeId : peerMap.keySet()) {
                if (nodeId.equals(selfId)) continue;
                PeerNode peerNode = peerMap.get(nodeId);
                if (peerNode.getNodeId() != selfId) indexs[pos] = peerNode.getMatchIndex();
                ++pos;
            }
            indexs[pos] = logMachine.getLastIndex();
            Arrays.sort(indexs);
            int midPos = peerMap.size() / 2;
            if (peerMap.size() % 2 == 0) {
                midPos = midPos - 1;
            }
            long midIndex = indexs[midPos];
            if (commitIndex < midIndex) {
                setCommitIndex(midIndex);
                writeData(midIndex);
                setLastApplied(midIndex);
                System.out.println("提交" + midIndex + "号日志");
            }
        } catch (InterruptedException | RocksDBException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            replicationLock.unlock();
        }
    }


    public void sendNullLogToAllPeer() {
        try {
            LogEntry[] logEntrys = new LogEntry[0];
            CountDownLatch countDownLatch = new CountDownLatch(peerMap.size() - 1);
            AtomicInteger successCount = new AtomicInteger(peerMap.size() - 1);
            for (String nodeId : peerMap.keySet()) {
                if (nodeId.equals(selfId)) continue;
                PeerNode peerNode = peerMap.get(nodeId);
                long nextIndex = peerNode.getNextIndex();
                long prevIndex = nextIndex - 1;
                long prevTeam = -1;
                LogEntry prevLog = null;
                prevLog = logMachine.getLog(prevIndex);
                if (prevLog != null) {
                    prevTeam = prevLog.getTerm();
                }
                AddEntryParam addEntryParam = new AddEntryParam(currentTerm,
                        selfId,
                        prevIndex,
                        prevTeam,
                        logEntrys,
                        commitIndex);
                RpcRequest req = new RpcRequest(RaftOrderType.LOG_REPLICATION,
                        addEntryParam,
                        peerNode.getAddress());
                submitLogReplicationRequestTask(peerNode, req, countDownLatch, successCount);
            }
            countDownLatch.await(600, TimeUnit.MILLISECONDS);
        } catch (RocksDBException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        }
    }

    /**
     * 检查选举超时的定时任务
     **/
    class ElectionTimeOutCheckTask implements Runnable {

        @Override
        public void run() {
            try {
                if (!checkElectionTimeOut()) {
                    return;
                }
                System.out.println(selfId + "发起选举");
                long lastIndex = logMachine.getLastIndex();
                LogEntry logEntry = logMachine.getLog(lastIndex);
                VoteParam voteParam = null;
                if (logEntry == null) {
                    voteParam = new VoteParam(currentTerm, selfId, lastIndex, -1);
                } else {
                    voteParam = new VoteParam(currentTerm, selfId, logEntry.getIndex(), logEntry.getTerm());
                }
                CountDownLatch countDownLatch = new CountDownLatch(peerMap.size() - 1);
                AtomicInteger successCount = new AtomicInteger(0);

                for (String nodeId : peerMap.keySet()) {
                    if (!nodeId.equals(selfId)) {
                        PeerNode peerNode = peerMap.get(nodeId);
                        RpcRequest rpcRequest = new RpcRequest(RaftOrderType.ELECTION, voteParam, peerNode.getAddress());
                        raftThreadPool.execute(new SendVoteRequestTask(countDownLatch, successCount, peerNode, rpcRequest));
                    }
                }
                countDownLatch.await(600, TimeUnit.MILLISECONDS);
                boolean electionResults = false;
                if (successCount.get() + 1 > peerMap.size() / 2) {
                    electionResults = setStatus(NodeStatus.leader);
                }
                if (electionResults) {
                    setLeaderId(selfId);
                    beReadyToBeALeader();
                    for (String nodeId : peerMap.keySet()) {
                        if (nodeId.equals(selfId)) continue;
                    }
                    sendNullLogToAllPeer();
                    System.out.println("当选leader");
                }
                setVotedFor(null);
            } catch (InterruptedException | RocksDBException e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            }
        }
    }


    /**
     * 发送选举请求的任务
     **/
    class SendVoteRequestTask implements Runnable {


        private CountDownLatch countDownLatch;

        private AtomicInteger successCount;

        private PeerNode peerNode;

        private RpcRequest rpcRequest;

        public SendVoteRequestTask(CountDownLatch countDownLatch,
                                   AtomicInteger successCount,
                                   PeerNode peerNode,
                                   RpcRequest rpcRequest) {
            this.countDownLatch = countDownLatch;
            this.successCount = successCount;
            this.peerNode = peerNode;
            this.rpcRequest = rpcRequest;
        }


        @Override
        public void run() {
            try {
                RpcResponse<VoteRes> rpcResponse = raftRpcClient.send(peerNode.getAddress(), rpcRequest);
                VoteRes voteRes = null;
                if (rpcResponse != null) {
                    voteRes = rpcResponse.getResult();
                    if (voteRes.getVoteGranted() == ResultCode.success) {
                        successCount.incrementAndGet();
                    } else {
                        if (voteRes.getTerm() > getCurrentTerm()) {
                            setCurrentTerm(voteRes.getTerm());
                            setStatus(NodeStatus.follower);
                        }
                    }
                }
            } catch (InterruptedException e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 发送日志复制请求的任务
     **/
    class LogReplicationRequestTask implements Runnable {

        private PeerNode peerNode;

        private RpcRequest rpcRequest;

        private CountDownLatch countDownLatch;

        private AtomicInteger successCount;

        public LogReplicationRequestTask(PeerNode peerNode,
                                         RpcRequest rpcRequest,
                                         CountDownLatch countDownLatch,
                                         AtomicInteger successCount) {
            this.peerNode = peerNode;
            this.rpcRequest = rpcRequest;
            this.countDownLatch = countDownLatch;
            this.successCount = successCount;
        }

        @Override
        public void run() {
            try {

                RpcResponse<AddEntryRes> rpcResponse = raftRpcClient.send(peerNode.getAddress(), rpcRequest);
                if (rpcResponse == null) {
                    return;
                }

                AddEntryRes addEntryRes = rpcResponse.getResult();
                PeerNode targetPeerNode = getPeerMap().get(peerNode.getNodeId());
                AddEntryParam addEntryParam = (AddEntryParam) rpcRequest.getMessage();

                if (addEntryRes.getSuccess() == ResultCode.fail) {
                    if (addEntryRes.getTerm() > getCurrentTerm()) {
                        setCurrentTerm(addEntryRes.getTerm());
                        setStatus(NodeStatus.follower);
                    } else {
                        targetPeerNode.setNextIndex(targetPeerNode.getNextIndex() - 1);
                    }
                } else {
                    LogEntry[] logEntries = addEntryParam.getLogEntries();
                    if (logEntries != null && logEntries.length != 0) {
                        LogEntry logEntry = logEntries[logEntries.length - 1];
                        targetPeerNode.setNextIndexAndMatchIndex(logEntry.getIndex() + 1, logEntry.getIndex());
                    } else {
                        targetPeerNode.setNextIndexAndMatchIndex(peerNode.getNextIndex(), peerNode.getNextIndex() - 1);
                    }
                    successCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            } finally {
                countDownLatch.countDown();
            }

        }
    }

    /**
     * 心跳任务
     **/
    class HeartBeatTask implements Runnable {

        @Override
        public void run() {
            try {
                if (getStatus() != NodeStatus.leader) {
                    return;
                }
                boolean checkLog = false;
                if (commitIndex < logMachine.getLastIndex()) {
                    checkLog = true;
                }
                for (String nodeId : peerMap.keySet()) {
                    PeerNode peerNode = peerMap.get(nodeId);
                    if (peerNode.getMatchIndex() < commitIndex) {
                        checkLog = true;
                    }
                }
                if (checkLog) {
                    sendLogToAllPeer();
                } else {
                    sendNullLogToAllPeer();
                }
            } catch (Exception e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            }
        }


    }


}








