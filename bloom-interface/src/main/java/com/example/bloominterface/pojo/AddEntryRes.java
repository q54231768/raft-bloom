package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class AddEntryRes implements Serializable {
    private long term;

    private int success;


    public AddEntryRes() {
    }

    public AddEntryRes(long term, int success) {
        this.term = term;
        this.success = success;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
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
