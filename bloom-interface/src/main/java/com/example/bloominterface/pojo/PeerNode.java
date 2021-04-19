package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class PeerNode implements Serializable {


    private String address;

    private String nodeId;

    private  long nextIndex;

    private  long matchIndex = -1;


    public PeerNode() {
    }

    public PeerNode(String address, String nodeId) {
        this.address = address;
        this.nodeId = nodeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(long nextIndex) {
        this.nextIndex = nextIndex;
    }

    public long getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(long matchIndex) {
        this.matchIndex = matchIndex;
    }

    public synchronized void setNextIndexAndMatchIndex(long nextIndex, long matchIndex) {
        if (matchIndex >= this.matchIndex) {
            this.nextIndex = nextIndex;
            this.matchIndex = matchIndex;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerNode peerNode = (PeerNode) o;
        return Objects.equals(address, peerNode.address) &&
                Objects.equals(nodeId, peerNode.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, nodeId);
    }

    @Override
    public String toString() {
        return "PeerNode{" +
                "address='" + address + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
