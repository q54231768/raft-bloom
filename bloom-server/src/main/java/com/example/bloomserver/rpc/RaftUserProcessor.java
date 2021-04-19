package com.example.bloomserver.rpc;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.common.ResultCode;
import com.example.bloominterface.pojo.AddEntryParam;
import com.example.bloominterface.pojo.ContentParam;
import com.example.bloominterface.pojo.VoteParam;
import com.example.bloominterface.pojo.VoteRes;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import com.example.bloomserver.raft.RaftConsistencyModule;
import com.example.bloomserver.raft.RaftNode;

import java.util.List;

public class RaftUserProcessor extends SyncUserProcessor<RpcRequest> {

    private RaftConsistencyModule raftConsistencyModule;


    public RaftUserProcessor(RaftConsistencyModule raftConsistencyModule) {
        this.raftConsistencyModule = raftConsistencyModule;
    }

    @Override
    public Object handleRequest(BizContext bizContext, RpcRequest rpcRequest) throws Exception {
        if (rpcRequest.getOrder() == RaftOrderType.ELECTION){
            return new RpcResponse(raftConsistencyModule.handleVoteRequest((VoteParam) rpcRequest.getMessage()));
        }else if (rpcRequest.getOrder() == RaftOrderType.LOG_REPLICATION){
            return new RpcResponse(raftConsistencyModule.handleAddEntryRes((AddEntryParam) rpcRequest.getMessage()));
        }else if(rpcRequest.getOrder() == RaftOrderType.CLIENT_REQ_WRITE){
            return new RpcResponse(raftConsistencyModule.handleClientWriteInEntryRequest((List<ContentParam>) rpcRequest.getMessage()));
        }else if(rpcRequest.getOrder() == RaftOrderType.CLIENT_REQ_READ_D){
            return new RpcResponse(raftConsistencyModule.handleClinetReadRequest(
                    (ContentParam) rpcRequest.getMessage(),
                    RaftOrderType.CLIENT_REQ_READ_D));
        }else if(rpcRequest.getOrder() == RaftOrderType.CLIENT_REQ_READ_C){
            return new RpcResponse(raftConsistencyModule.handleClinetReadRequest(
                    (ContentParam) rpcRequest.getMessage(),
                    RaftOrderType.CLIENT_REQ_READ_C));
        }else if(rpcRequest.getOrder() == RaftOrderType.CLIENT_REQ_GET_LEADER_ID){
            return new RpcResponse(raftConsistencyModule.handleClinetGetLeaderRequest(
                    (ContentParam) rpcRequest.getMessage()));
        }
        return null;
    }

    @Override
    public String interest() {
        return RpcRequest.class.getName();
    }
}
