package com.example.bloomclient.controller;


import com.example.bloomclient.client.BloomClient;
import com.example.bloominterface.pojo.BloomFilterSituation;
import com.example.bloominterface.pojo.NodeStatusList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired(required = false)
    private BloomClient bloomClient;


    @RequestMapping("adminManage")
    public String adminManage() {
        return "adminManage";
    }

    /**
     * 获取布隆过滤器状态
     *
     * @param
     */
    @RequestMapping("getBloomFilterStatus")
    @ResponseBody
    public BloomFilterSituation getBloomFilterStatus() {
        return bloomClient.getServerConfig();
    }

    /**
     * 单个插入
     *
     * @param
     */
    @RequestMapping("singleInsert")
    @ResponseBody
    public int singleInsert(@RequestParam("content") String content) {
        try {
            if (!bloomClient.write(content)) return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 批量插入
     *
     * @param
     */
    @RequestMapping("batchInsert")
    @ResponseBody
    public Object batchInsert(HttpServletRequest request) throws IOException {
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("txtfile");
        Reader reader = null;
        BufferedReader br = null;
        Map<String, Object> map = new HashMap<>();
        List<String> strList = new ArrayList<>();
        try {
            reader = new InputStreamReader(file.getInputStream(), "utf-8");
            br = new BufferedReader(reader);
            long start = System.currentTimeMillis();
            String line = null;
            int count = 0;
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                ++count;
                strList.add(line);
            }
            if (bloomClient.batchWrite(strList)) {
                map.put("result", "success");
            } else {
                map.put("result", "fail");
                return map;
            }
            long end = System.currentTimeMillis();
            map.put("takeUpTime", end - start);
            map.put("count", count);
        } catch (IOException e) {
            e.printStackTrace();
            map.put("result", "fail");
            return map;
        } finally {
            br.close();
        }
        return map;
    }

    /**
     * 重置过滤器
     *
     * @param
     */
    @RequestMapping("reset")
    @ResponseBody
    public String reset(@RequestParam("expectedQuantity") int expectedQuantity,
                        @RequestParam("misjudgmentRate") double misjudgmentRate) {
        if (misjudgmentRate < 0 || misjudgmentRate >= 1) {
            return "充值失败,请检查输入参数是否合法";
        }
        bloomClient.reset(expectedQuantity, misjudgmentRate);
        return "重置成功";
    }


    /**
     * 检查字符串是否包含
     *
     * @param
     */
    @RequestMapping("checkIfContain")
    @ResponseBody
    public String checkIfContain(@RequestParam("content") String content) {
        int res = bloomClient.read(content);
        if (res == 1) {
            return "可能包含";
        } else if (res == 0) {
            return "不包含";
        } else {
            return "系统错误";
        }
    }

    @RequestMapping("getNodeStatus")
    @ResponseBody
    public NodeStatusList getNodeStatus() {
        return new NodeStatusList(bloomClient.getAliveMap(), bloomClient.getLeaderId());
    }


}
