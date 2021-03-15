package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class VoteParam implements Serializable {

    private long term;

    private String candidateId;

    private long lastLogIndex;

    private long lastLogTerm;


    public VoteParam(long term, String candidateId, long lastLogIndex, long lastLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public long getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(long lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteParam voteParam = (VoteParam) o;
        return term == voteParam.term &&
                lastLogIndex == voteParam.lastLogIndex &&
                lastLogTerm == voteParam.lastLogTerm &&
                Objects.equals(candidateId, voteParam.candidateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, candidateId, lastLogIndex, lastLogTerm);
    }

    @Override
    public String toString() {
        return "VoteParam{" +
                "term=" + term +
                ", candidateId='" + candidateId + '\'' +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }
}
