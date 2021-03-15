package com.example.bloominterface.request;

import java.io.Serializable;
import java.util.Objects;

public class RpcRequest<T> implements Serializable {

    //请求类型
    private int order;

    //请求信息体
    private T message;

    //接收请求的服务器地址
    private String to;


    public RpcRequest() {
    }


    public RpcRequest(int order, T message, String to) {
        this.order = order;
        this.message = message;
        this.to = to;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcRequest<?> that = (RpcRequest<?>) o;
        return order == that.order &&
                Objects.equals(message, that.message) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, message, to);
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "order=" + order +
                ", message=" + message +
                ", to='" + to + '\'' +
                '}';
    }
}
