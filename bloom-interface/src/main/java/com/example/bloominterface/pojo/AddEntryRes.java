package com.example.bloominterface.pojo;

import java.util.Objects;

public class AddEntryRes {
    private long term;

    private long success;


    public AddEntryRes() {
    }

    public AddEntryRes(long term, long success) {
        this.term = term;
        this.success = success;
    }


    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddEntryRes that = (AddEntryRes) o;
        return term == that.term &&
                success == that.success;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, success);
    }

    @Override
    public String toString() {
        return "AddEntryRes{" +
                "term=" + term +
                ", success=" + success +
                '}';
    }
}
