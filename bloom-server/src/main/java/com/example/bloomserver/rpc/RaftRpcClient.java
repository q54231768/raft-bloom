package com.example.bloomserver.rpc;

import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import com.example.bloomserver.exception.ExceptionHandler;
import com.example.bloomserver.exception.impl.DefaultExceptionHandler;
import com.example.bloomserver.raft.RaftLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaftRpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaftRpcClient.class);

    private RpcClient rpcClient;

    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

    public boolean init() {
        rpcClient.init();
        return true;
    }

    public RpcResponse send(String address, RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = (RpcResponse) rpcClient.invokeSync(address, rpcRequest, 3000);
        } catch (RemotingException|InterruptedException e) {
            exceptionHandler.handle(e,LOGGER);
        }
        return rpcResponse;
    }


}
