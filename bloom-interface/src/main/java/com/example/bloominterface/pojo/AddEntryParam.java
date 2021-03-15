package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class AddEntryParam implements Serializable {

    private long term;

    private String leaderId;

    private long prevLogIndex;

    private long prevLogTerm;

    private LogEntry logEntries[];

    private long leaderCommit;

    public AddEntryParam() {
    }

    public AddEntryParam(long term, String leaderId, long prevLogIndex, long prevLogTerm, LogEntry[] logEntries, long leaderCommit) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.logEntries = logEntries;
        this.leaderCommit = leaderCommit;
    }


    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public long getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(long prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public LogEntry[] getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(LogEntry[] logEntries) {
        this.logEntries = logEntries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddEntryParam that = (AddEntryParam) o;
        return term == that.term &&
                prevLogIndex == that.prevLogIndex &&
                prevLogTerm == that.prevLogTerm &&
                leaderCommit == that.leaderCommit &&
                Objects.equals(leaderId, that.leaderId) &&
                Arrays.equals(logEntries, that.logEntries);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(term, leaderId, prevLogIndex, prevLogTerm, leaderCommit);
        result = 31 * result + Arrays.hashCode(logEntries);
        return result;
    }


    @Override
    public String toString() {
        return "AddEntryParam{" +
                "term=" + term +
                ", leaderId='" + leaderId + '\'' +
                ", prevLogIndex=" + prevLogIndex +
                ", prevLogTerm=" + prevLogTerm +
                ", logEntries=" + Arrays.toString(logEntries) +
                ", leaderCommit=" + leaderCommit +
                '}';
    }
}
