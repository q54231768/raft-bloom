package com.example.bloomclient.client;

import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.example.bloomclient.ThreadPool.RaftThreadPool;
import com.example.bloominterface.common.CommandType;
import com.example.bloominterface.common.RaftOrderType;
import com.example.bloominterface.common.ResultCode;
import com.example.bloominterface.common.SourceType;
import com.example.bloominterface.exception.GlobalLoggerHandler;
import com.example.bloominterface.pojo.*;
import com.example.bloominterface.request.RpcRequest;
import com.example.bloominterface.response.RpcResponse;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class BloomClient {


    private static final Logger LOGGER = null;

    private RpcClient rpcClient;


    private Map<String, PeerNode> peerNodeMap;

    private Map<String, Integer> aliveMap;

    private String leaderId;

    private BloomFilterSituation bloomFilterSituation;

    private RaftThreadPool raftThreadPool;

    public BloomClient(Map<String, PeerNode> peerNodeMap, String leaderId, RaftThreadPool raftThreadPool) {
        this.peerNodeMap = peerNodeMap;
        aliveMap = new HashMap<>();
        for (String nodeId : peerNodeMap.keySet()) {
            aliveMap.put(nodeId, 5);
        }
        this.leaderId = leaderId;
        this.raftThreadPool = raftThreadPool;
        raftThreadPool.init();
        this.raftThreadPool.scheduleWithFixedRate(new UpdateLeaderIdTask(),5000,2000);
    }

    public boolean init() {
        rpcClient = new RpcClient();
        rpcClient.init();
        return true;
    }

    public Map<String, PeerNode> getPeerNodeMap() {
        return peerNodeMap;
    }

    public void setPeerNodeMap(Map<String, PeerNode> peerNodeMap) {
        this.peerNodeMap = peerNodeMap;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }


    public Map<String, Integer> getAliveMap() {
        return aliveMap;
    }

    public void setAliveMap(Map<String, Integer> aliveMap) {
        this.aliveMap = aliveMap;
    }

    public RpcResponse send(String address, RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = (RpcResponse) rpcClient.invokeSync(address, rpcRequest, 5000);
            System.out.println(rpcResponse);
        } catch (RemotingException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
            System.out.println("超时了");
        }
        return rpcResponse;
    }

    public boolean write(String value) {
        if (leaderId == null) return false;
        ContentParam<String> contentParam = new ContentParam<>(value, CommandType.WRITE, SourceType.CLIENT);
        List<ContentParam> list = new ArrayList<>();
        list.add(contentParam);
        String machineAddress = peerNodeMap.get(leaderId).getAddress();
        RpcRequest rpcRequest = new RpcRequest(
                RaftOrderType.CLIENT_REQ_WRITE, list,
                machineAddress);
        RpcResponse<ClientCommandRes> rpcResponse = send(machineAddress, rpcRequest);
        ClientCommandRes clientCommandRes = rpcResponse.getResult();
        if (clientCommandRes.getResult() == ResultCode.success) {
            System.out.println(clientCommandRes);
            System.out.println("写入成功");
            return true;
        } else {
            return false;
        }
    }

    public boolean batchWrite(List<String> values){
        if (leaderId == null) return false;
        List<ContentParam> valueList = new ArrayList<>();
        for(int i=0;i<values.size();++i){
            ContentParam<String> contentParam =  new ContentParam<>(values.get(i), CommandType.WRITE, SourceType.CLIENT);
            valueList.add(contentParam);
        }
        String address = peerNodeMap.get(leaderId).getAddress();
        RpcRequest rpcRequest = new RpcRequest(
                RaftOrderType.CLIENT_REQ_WRITE, valueList,
                address);
        RpcResponse<ClientCommandRes> rpcResponse = send(address, rpcRequest);
        ClientCommandRes clientCommandRes = rpcResponse.getResult();
        if (clientCommandRes.getResult() == ResultCode.success) {
            System.out.println(clientCommandRes);
            System.out.println("写入成功");
            return true;
        } else {
            return false;
        }
    }



    public boolean reset(long expectedQuantity, double misjudgmentRate) {
        String expectedQuantityStr = expectedQuantity + "";
        String misjudgmentRateStr = misjudgmentRate + "";
        BloomFilterConfigMap bloomFilterConfigMap = new BloomFilterConfigMap(expectedQuantityStr, misjudgmentRateStr);
        ContentParam<String> contentParam = new ContentParam(bloomFilterConfigMap, CommandType.RESET, SourceType.CLIENT);
        List<ContentParam> arrList =  new ArrayList<>();
        arrList.add(contentParam);
        String machineAddress = peerNodeMap.get(leaderId).getAddress();
        RpcRequest rpcRequest = new RpcRequest(
                RaftOrderType.CLIENT_REQ_WRITE, arrList,
                machineAddress);
        RpcResponse<ClientCommandRes> rpcResponse = send(machineAddress, rpcRequest);
        ClientCommandRes clientCommandRes = rpcResponse.getResult();
        if (clientCommandRes.getResult() == ResultCode.success) {
            System.out.println(clientCommandRes);
            System.out.println("重置成功");
            return true;
        } else {
            return false;
        }
    }

    public int read(String value) {
        if (leaderId == null) return 2;
        ContentParam<String> contentParam = new ContentParam<>(value, CommandType.READ, SourceType.CLIENT);
        String machineAddress = peerNodeMap.get(leaderId).getAddress();
        RpcRequest rpcRequest = new RpcRequest(
                RaftOrderType.CLIENT_REQ_READ_D, contentParam,
                machineAddress);
        RpcResponse<ClientCommandRes> rpcResponse = send(machineAddress, rpcRequest);
        ClientCommandRes clientCommandRes = rpcResponse.getResult();
        if (clientCommandRes.getResult() == ResultCode.success) {
            System.out.println(clientCommandRes);
            System.out.println("读取成功");
            return 1;
        } else if(clientCommandRes.getResult() == ResultCode.fail){
            return 0;
        } else{
            return 2;
        }
    }

    public BloomFilterSituation getServerConfig() {
        if (leaderId == null) return null;
        ContentParam<String> contentParam = new ContentParam<>(null, CommandType.READ, SourceType.CLIENT);
        String machineAddress = peerNodeMap.get(leaderId).getAddress();
        RpcRequest rpcRequest = new RpcRequest(
                RaftOrderType.CLIENT_REQ_READ_C, contentParam,
                machineAddress);
        RpcResponse<ClientCommandRes> rpcResponse = send(machineAddress, rpcRequest);
        ClientCommandRes clientCommandRes = rpcResponse.getResult();
        if (clientCommandRes.getResult() == ResultCode.success) {
            System.out.println(clientCommandRes);
            System.out.println("读取成功");
            bloomFilterSituation = (BloomFilterSituation) clientCommandRes.getDetails();
            return bloomFilterSituation;
        } else {
            return null;
        }
    }


    class LeaderIdResult {
        private String leaderId;

        public LeaderIdResult() {
        }

        public LeaderIdResult(String leaderId) {
            this.leaderId = leaderId;
        }

        public String getLeaderId() {
            return leaderId;
        }

        public void setLeaderId(String leaderId) {
            this.leaderId = leaderId;
        }
    }


    class UpdateLeaderIdTask implements Runnable {

        @Override
        public void run() {
            try {
                CountDownLatch countDownLatch = new CountDownLatch(peerNodeMap.size());
                List<LeaderIdResult> list = new ArrayList<>();
                for (int i = 0; i < peerNodeMap.size(); ++i) {
                    list.add(new LeaderIdResult());
                }
                int i = 0;
                for (String nodeId : peerNodeMap.keySet()) {
                    PeerNode peerNode = peerNodeMap.get(nodeId);
                    raftThreadPool.execute(new GetLeaderIdRequsetTask(countDownLatch, peerNode, list.get(i)));
                    ++i;
                }
                countDownLatch.await(3000, TimeUnit.MILLISECONDS);
                Map<String, Integer> map = new HashMap<>();
                for (int j = 0; j < list.size(); ++j) {
                    String nodeId = list.get(j).getLeaderId();
                    if (map.containsKey(nodeId)) {
                        int count = map.get(nodeId);
                        ++count;
                        map.put(nodeId, count);
                    } else {
                        map.put(nodeId, 1);
                    }
                }
                for (String nodeId : map.keySet()) {
                    if (map.get(nodeId) > peerNodeMap.size() / 2) {
                        leaderId = nodeId;
                    }
                }
            } catch (InterruptedException e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            }
        }
    }


    class GetLeaderIdRequsetTask implements Runnable {

        private CountDownLatch countDownLatch;

        private PeerNode peerNode;

        private LeaderIdResult leaderIdResult;


        public GetLeaderIdRequsetTask(CountDownLatch countDownLatch,
                                      PeerNode peerNode,
                                      LeaderIdResult leaderIdResult) {
            this.countDownLatch = countDownLatch;
            this.peerNode = peerNode;
            this.leaderIdResult = leaderIdResult;
        }

        @Override
        public void run() {
            try {
                ContentParam contentParam = new ContentParam(null, CommandType.GET_LEADERID, SourceType.CLIENT);
                RpcRequest rpcRequest = new RpcRequest(
                        RaftOrderType.CLIENT_REQ_GET_LEADER_ID,
                        contentParam,
                        peerNode.getAddress());
                RpcResponse rpcResponse = send(peerNode.getAddress(), rpcRequest);
                String leaderId = null;
                if (rpcResponse != null) {
                    ClientCommandRes res = (ClientCommandRes) rpcResponse.getResult();
                    leaderId = (String) res.getDetails();
                    aliveMap.put(peerNode.getNodeId(), 5);
                } else {
                    int value = aliveMap.get(peerNode.getNodeId());
                    if(value > 0) --value;
                    aliveMap.put(peerNode.getNodeId(), value);
                }
                leaderIdResult.setLeaderId(leaderId);
            } catch (Exception e) {
                GlobalLoggerHandler.handle(e, LOGGER);
            } finally {
                countDownLatch.countDown();
            }
        }
    }


}
