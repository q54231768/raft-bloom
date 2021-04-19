package com.example.bloomserver.raft;

import com.alibaba.fastjson.JSON;
import com.example.bloominterface.common.CommandType;
import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.common.ResultCode;
import com.example.bloominterface.common.SourceType;
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
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RaftConsistencyModule {

    //    private static final Logger LOGGER = LoggerFactory.getLogger(RaftConsistencyModule.class);
    private static final Logger LOGGER = null;


    private RaftNode raftNode;


    public RaftConsistencyModule(RaftNode raftNode) {
        this.raftNode = raftNode;
    }


    /**
     * 处理来自候选者的投票请求
     *
     * @param voteParam
     * @return VoteRes
     */
    public VoteRes handleVoteRequest(VoteParam voteParam) {
        VoteRes voteRes = null;
        try {
            raftNode.getVoteRock().tryLock(300, TimeUnit.MILLISECONDS);
            long currentTerm = raftNode.getCurrentTerm();
            if (raftNode.getCurrentTerm() > voteParam.getTerm()) {
                voteRes = new VoteRes(currentTerm, ResultCode.fail);
                return voteRes;
            }
            String votedFor = raftNode.getVotedFor();
            RaftLog logMachine = raftNode.getLogMachine();
            if (votedFor != null && (!votedFor.equals(voteParam.getCandidateId()))) {
                voteRes = new VoteRes(currentTerm, ResultCode.fail);
            } else {
                long lastIndex = logMachine.getLastIndex();
                LogEntry lastLogEntry = logMachine.getLog(lastIndex);
                long lastTerm = -1;
                if (lastLogEntry != null) lastTerm = lastLogEntry.getTerm();
                if (voteParam.getLastLogTerm() > lastTerm || (voteParam.getLastLogTerm() == lastTerm && voteParam.getLastLogIndex() >= lastIndex)) {
                    voteRes = new VoteRes(currentTerm, ResultCode.success);
                    raftNode.setVotedFor(voteParam.getCandidateId());
                    raftNode.setCurrentTerm(voteParam.getTerm());
                    raftNode.setStatus(NodeStatus.follower);
                    raftNode.setLeaderId(voteParam.getCandidateId());
                    raftNode.setLastHeartBeatTime(System.currentTimeMillis());
                    System.out.println("投票给" + voteParam.getCandidateId());
                } else {
                    voteRes = new VoteRes(currentTerm, ResultCode.fail);
                }
            }
        } catch (InterruptedException | RocksDBException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
            voteRes = new VoteRes(raftNode.getCurrentTerm(), ResultCode.fail);
        } finally {
            raftNode.getVoteRock().unlock();
        }
        return voteRes;
    }

    /**
     * 处理来自leader的日志复制请求
     *
     * @param addEntryParam
     * @return AddEntryRes
     */
    public AddEntryRes handleAddEntryRes(AddEntryParam addEntryParam) {
        AddEntryRes addEntryRes = null;
        try {
            raftNode.getReplicationLock().tryLock(300, TimeUnit.MILLISECONDS);
            raftNode.getVoteRock().tryLock(300, TimeUnit.MILLISECONDS);
            long term = addEntryParam.getTerm();
            if (raftNode.getCurrentTerm() > term) {
                System.out.println("leader任期太小");
                addEntryRes = new AddEntryRes(raftNode.getCurrentTerm(), ResultCode.fail);
                raftNode.getVoteRock().unlock();
                return addEntryRes;
            }
            raftNode.setCurrentTerm(term);
            raftNode.setStatus(NodeStatus.follower);
            raftNode.setLeaderId(addEntryParam.getLeaderId());
            raftNode.setLastHeartBeatTime(System.currentTimeMillis());
            raftNode.getVoteRock().unlock();
            if (addEntryParam.getLogEntries() == null || addEntryParam.getLogEntries().length == 0)
                System.out.println("收到来自leader - " + addEntryParam.getLeaderId() + "的心跳");
            else System.out.println("收到来自leader - " + addEntryParam.getLeaderId() + "的日志复制");

            LogEntry previousLog = null;
            RaftLog raftLog = raftNode.getLogMachine();
            if (addEntryParam.getPrevLogIndex() > -1) {
                previousLog = raftLog.getLog(addEntryParam.getPrevLogIndex());
                if (previousLog == null) {
                    addEntryRes = new AddEntryRes(raftNode.getCurrentTerm(), ResultCode.fail);
                    return addEntryRes;
                }
                if (previousLog.getTerm() != addEntryParam.getPrevLogTerm()) {
                    long nowLastIndex = raftLog.getLastIndex();
                    raftLog.batchDeleteLog(addEntryParam.getPrevLogIndex());
                    raftLog.updateLastIndex(addEntryParam.getPrevLogIndex() - 1);
                    addEntryRes = new AddEntryRes(raftNode.getCurrentTerm(), ResultCode.fail);
                    return addEntryRes;
                }
            }

            LogEntry[] logEntrys = addEntryParam.getLogEntries();
            if (logEntrys != null && logEntrys.length > 0) {
                long conflictIndex = raftLog.checkLogEntrys(logEntrys);
                int conflictPos = (int) (conflictIndex - logEntrys[0].getIndex());
                LogEntry[] logEntriesToWrite = null;
                if (conflictPos == 0) {
                    logEntriesToWrite = logEntrys;
                }
                if (conflictPos < logEntrys.length && conflictPos > 0) {
                    logEntriesToWrite = Arrays.copyOfRange(logEntrys, conflictPos, logEntrys.length);
                }
                raftLog.batchWriteLog(logEntriesToWrite);
//                System.out.println(addEntryParam);
            }
            long lastIndex = -1;
            long commitIndex = raftNode.getCommitIndex();
            long leaderCommitIndex = addEntryParam.getLeaderCommit();
            if (leaderCommitIndex > commitIndex) {
                lastIndex = raftLog.getLastIndex();
                long minIndex = lastIndex;
                if (minIndex > leaderCommitIndex) {
                    minIndex = leaderCommitIndex;
                }
                raftNode.setCommitIndex(minIndex);
                raftNode.writeData(minIndex);
                raftNode.setLastApplied(minIndex);
                System.out.println("提交" + minIndex + "日志");
            }
            addEntryRes = new AddEntryRes(raftNode.getCurrentTerm(), ResultCode.success);
        } catch (InterruptedException | RocksDBException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            raftNode.getReplicationLock().unlock();
        }
        return addEntryRes;
    }

    /**
     * 处理来自客户端的写入请求
     *
     * @param contentParamList
     * @return ClientCommandRes
     */
    public ClientCommandRes handleClientWriteInEntryRequest(List<ContentParam> contentParamList) {
        ClientCommandRes clientCommandRes = new ClientCommandRes(ResultCode.fail);
        try {
            raftNode.getReplicationLock().tryLock(300, TimeUnit.MILLISECONDS);
            if (raftNode.getStatus() != NodeStatus.leader) return clientCommandRes;
            RaftLog raftLog = raftNode.getLogMachine();
            long currentTeam = raftNode.getCurrentTerm();
            String content = null;
            LogEntry[] newLogEntrys = new LogEntry[contentParamList.size()];
            for (int i = 0; i < contentParamList.size(); ++i) {
                ContentParam contentParam = contentParamList.get(i);
                if (contentParam.getCommandType() == CommandType.WRITE) {
                    content = (String) contentParam.getValue();
                } else {
                    BloomFilterConfigMap bloomFilterConfigMap = (BloomFilterConfigMap) contentParam.getValue();
                    content = JSON.toJSONString(bloomFilterConfigMap);
                }
                LogEntry logEntry = new LogEntry(raftLog.getLastIndex() + 1, currentTeam, contentParam.getCommandType(), content);
                newLogEntrys[i] = logEntry;
            }
            if(!raftLog.batchWriteLog(newLogEntrys)) return  clientCommandRes;
            Map<String, PeerNode> peerNodeMap = raftNode.getPeerMap();
            long lastIndex = raftLog.getLastIndex();
            AtomicInteger successcount = new AtomicInteger(0);
            CountDownLatch countDownLatch = new CountDownLatch(peerNodeMap.size() - 1);
            for (String nodeId : peerNodeMap.keySet()) {
                if (nodeId.equals(raftNode.getSelfId())) continue;
                PeerNode peerNode = peerNodeMap.get(nodeId);
                long nextIndex = peerNode.getNextIndex();
                long prevLogIndex = nextIndex - 1;
                long prevLogTerm = -1;
                if (prevLogIndex != -1) {
                    LogEntry prevlogEntry = raftLog.getLog(prevLogIndex);
                    prevLogTerm = prevlogEntry.getTerm();
                }
                LogEntry logEntrys[] = new LogEntry[(int) (lastIndex - prevLogIndex)];
                int j = 0;
                for (long i = nextIndex; i <= lastIndex; ++i, ++j) {
                    logEntrys[j] = raftLog.getLog(i);
                }
                AddEntryParam addEntryParam = new AddEntryParam(
                        currentTeam,
                        raftNode.getSelfId(),
                        prevLogIndex,
                        prevLogTerm,
                        logEntrys,
                        raftNode.getCommitIndex());

                RpcRequest req = new RpcRequest(RaftOrderType.LOG_REPLICATION, addEntryParam, peerNode.getAddress());
                raftNode.submitLogReplicationRequestTask(peerNode, req, countDownLatch, successcount);
            }
            countDownLatch.await(700, TimeUnit.MILLISECONDS);

            if (successcount.get() + 1 > peerNodeMap.size() / 2) {
                raftNode.setCommitIndex(lastIndex);
                raftNode.writeData(lastIndex);
                raftNode.setLastApplied(lastIndex);
                clientCommandRes.setResult(ResultCode.success);
//                System.out.println(contentParamList);
            }


        } catch (InterruptedException | RocksDBException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            raftNode.getReplicationLock().unlock();
        }
        return clientCommandRes;
    }

    /**
     * 处理来自客户端的读取请求
     *
     * @param contentParam
     * @return ClientCommandRes
     */
    public ClientCommandRes handleClinetReadRequest(ContentParam contentParam, int raftOrderType) {
        ClientCommandRes clientCommandRes = null;
        if (raftNode.getStatus() != NodeStatus.leader) {
            if (contentParam.getFrom() == SourceType.CLIENT) {
                String leaderId = raftNode.getLeaderId();
                if (leaderId == null) {
                    clientCommandRes = new ClientCommandRes(ResultCode.error);
                    return clientCommandRes;
                }
                ContentParam newContentParam = new ContentParam(contentParam.getValue(),
                        contentParam.getCommandType(),
                        SourceType.SERVER);
                PeerNode leaderNode = raftNode.getPeerMap().get(leaderId);
                RpcRequest rpcRequest = new RpcRequest(raftOrderType,
                        newContentParam,
                        leaderNode.getAddress());
                RaftRpcClient raftRpcClient = raftNode.getRaftRpcClient();
                RpcResponse response = raftRpcClient.send(leaderNode.getAddress(), rpcRequest);
                clientCommandRes = (ClientCommandRes) response.getResult();
                return clientCommandRes;
            }
        } else {
            if (raftOrderType == RaftOrderType.CLIENT_REQ_READ_D) {
                return raftNode.readData(contentParam.getValue());
            } else if (raftOrderType == RaftOrderType.CLIENT_REQ_READ_C) {
                return raftNode.getServerConfig(contentParam.getValue());
            }
        }
        clientCommandRes = new ClientCommandRes(ResultCode.error);
        return clientCommandRes;
    }


    /**
     * 处理来自客户端的获取leaderId请求
     *
     * @param contentParam
     * @return ClientCommandRes
     */
    public ClientCommandRes handleClinetGetLeaderRequest(ContentParam contentParam) {
        String leaderId = raftNode.getLeaderId();
        return new ClientCommandRes(ResultCode.success, leaderId);
    }

}
