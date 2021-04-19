package com.example.bloominterface.pojo;

import com.sun.javafx.collections.MappingChange;
import java.util.*;
import java.io.Serializable;

public class NodeStatusList implements Serializable {
    private Map<String,Integer> aliveMap;

    private String leaderId;

    public NodeStatusList(Map<String, Integer> aliveMap, String leaderId) {
        this.aliveMap = aliveMap;
        this.leaderId = leaderId;
    }

    public Map<String, Integer> getAliveMap() {
        return aliveMap;
    }

    public void setAliveMap(Map<String, Integer> aliveMap) {
        this.aliveMap = aliveMap;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeStatusList that = (NodeStatusList) o;
        return Objects.equals(aliveMap, that.aliveMap) &&
                Objects.equals(leaderId, that.leaderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliveMap, leaderId);
    }

    @Override
    public String toString() {
        return "NodeStatusList{" +
                "aliveMap=" + aliveMap +
                ", leaderId='" + leaderId + '\'' +
                '}';
    }
}
