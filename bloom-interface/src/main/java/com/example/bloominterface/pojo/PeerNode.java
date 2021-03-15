package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class PeerNode implements Serializable {


    private boolean isSelf;

    private boolean isLeader;

    private String address;

    private String nodeId;

    public PeerNode() {
    }

    public PeerNode(boolean isSelf, boolean isLeader, String address) {
        this.isSelf = isSelf;
        this.isLeader = isLeader;
        this.address = address;
        this.nodeId = address;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerNode peerNode = (PeerNode) o;
        return isSelf == peerNode.isSelf &&
                isLeader == peerNode.isLeader &&
                Objects.equals(address, peerNode.address) &&
                Objects.equals(nodeId, peerNode.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSelf, isLeader, address, nodeId);
    }

    @Override
    public String toString() {
        return "PeerNode{" +
                "isSelf=" + isSelf +
                ", isLeader=" + isLeader +
                ", address='" + address + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
