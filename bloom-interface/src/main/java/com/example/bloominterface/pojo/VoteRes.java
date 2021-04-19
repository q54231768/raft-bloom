package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class VoteRes implements Serializable {


    private long term;

    private int voteGranted;

    public VoteRes() {
    }

    public VoteRes(long term, int voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public int getVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(int voteGranted) {
        this.voteGranted = voteGranted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteRes voteRes = (VoteRes) o;
        return term == voteRes.term &&
                voteGranted == voteRes.voteGranted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, voteGranted);
    }

    @Override
    public String toString() {
        return "VoteRes{" +
                "term=" + term +
                ", voteGranted=" + voteGranted +
                '}';
    }
}
