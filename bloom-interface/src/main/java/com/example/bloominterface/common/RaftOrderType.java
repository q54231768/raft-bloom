package com.example.bloominterface.common;

import sun.rmi.runtime.Log;

public class RaftOrderType {

    //日志复制
    public static int LOG_REPLICATION = 100;

    //选举
    public static int ELECTION = 200;


    //客户端写入请求
    public static int CLIENT_REQ_WRITE = 300;


    //客户端读数据请求
    public static int CLIENT_REQ_READ_D = 400;

    //客户端读取配置请求
    public static int CLIENT_REQ_READ_C = 500;

    //客户端获取leaderId
    public static int CLIENT_REQ_GET_LEADER_ID = 600;


}
