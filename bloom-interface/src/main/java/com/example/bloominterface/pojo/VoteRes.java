package com.example.bloominterface.pojo;

import java.util.Objects;

public class VoteRes {


    private long term;

    private long voteGranted;


    public VoteRes() {
    }

    public VoteRes(long term, long voteGranted) {
        this.term = term;
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
