package com.example.bloomserver.rpc;

import com.alipay.remoting.rpc.RpcServer;

public class RaftRpcServer {

    public static volatile int isServerStart = 0;

    public static RaftRpcServer raftRpcServer;


    public static RaftRpcServer getRaftRpcServer(int port, RaftUserProcessor raftUserProcessor) {

        if (isServerStart == 0) {
            synchronized (com.example.bloomserver.rpc.RaftRpcServer.class) {
                if (isServerStart == 0) {
                    RaftRpcServer server = new RaftRpcServer();
                    if (server.init(port, raftUserProcessor) == false) {
                        return null;
                    } else {
                        raftRpcServer = server;
                    }
                }
            }

        }
        return raftRpcServer;
    }


    private int port;

    private RpcServer rpcServer;

    public RaftRpcServer() {
    }

    public boolean init(int port, RaftUserProcessor raftUserProcessor) {
        this.port = port;
        rpcServer = new RpcServer(port);
        rpcServer.registerUserProcessor(raftUserProcessor);
        return rpcServer.start();
    }


}
