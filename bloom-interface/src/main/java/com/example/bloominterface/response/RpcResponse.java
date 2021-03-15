package com.example.bloominterface.response;

import java.io.Serializable;
import java.util.Objects;

public class RpcResponse<T> implements Serializable {

    private T result;

    public RpcResponse() {
    }

    public RpcResponse(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcResponse<?> that = (RpcResponse<?>) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "result=" + result +
                '}';
    }
}
