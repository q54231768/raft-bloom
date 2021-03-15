package com.example.bloomserver.rpc;

import com.alipay.remoting.rpc.RpcServer;

public class RaftRpcServer {

    public static volatile int isServerStart = 0;

    public static RaftRpcServer raftRpcServer;


    public static RaftRpcServer getRaftRpcServer(int port){

        if(isServerStart == 0){
            synchronized (com.example.bloomserver.rpc.RaftRpcServer.class){
                if(isServerStart == 0){
                    RaftRpcServer server = new RaftRpcServer();
                    if(server.init(port) == false){
                        return null;
                    }else{
                        raftRpcServer = server;
                    }
                }
            }

        }
        return raftRpcServer;
    }


    private int port;

    private RpcServer rpcServer;


    public boolean init(int port) {
        this.port = port;
        rpcServer = new RpcServer(port, false, false);
        return rpcServer.start();
    }








}
