package com.example.bloomserver.rpc;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloomserver.raft.RaftNode;

public class RaftUserProcessor extends SyncUserProcessor<RpcRequest> {

    private RaftNode raftNode;


    public RaftUserProcessor(RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    @Override
    public Object handleRequest(BizContext bizContext, RpcRequest rpcRequest) throws Exception {



        return null;
    }

    @Override
    public String interest() {
        return RpcRequest.class.getName();
    }
}
