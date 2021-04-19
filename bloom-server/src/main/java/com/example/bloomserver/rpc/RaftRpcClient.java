package com.example.bloomserver.rpc;

import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.example.bloominterface.exception.GlobalLoggerHandler;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaftRpcClient {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RaftRpcClient.class);

    private static final Logger LOGGER = null;

    private RpcClient rpcClient;


    public boolean init() {
        rpcClient = new RpcClient();
        rpcClient.init();
        return true;
    }

    public RpcResponse send(String address, RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = (RpcResponse) rpcClient.invokeSync(address, rpcRequest, 500);
            System.out.println(rpcResponse);
        } catch (RemotingException|InterruptedException e) {
            GlobalLoggerHandler.handle(e,LOGGER);
        }
        return rpcResponse;
    }


}
