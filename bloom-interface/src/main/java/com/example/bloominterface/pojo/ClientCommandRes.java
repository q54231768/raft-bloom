package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class ClientCommandRes<T> implements Serializable {
    private int result;

    private T details;

    public ClientCommandRes(int result, T details) {
        this.result = result;
        this.details = details;
    }

    public ClientCommandRes(int result) {
        this.result = result;
    }


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public T getDetails() {
        return details;
    }

    public void setDetails(T details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientCommandRes<?> that = (ClientCommandRes<?>) o;
        return result == that.result &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, details);
    }

    @Override
    public String toString() {
        return "ClientCommandRes{" +
                "result=" + result +
                ", details=" + details +
                '}';
    }
}
