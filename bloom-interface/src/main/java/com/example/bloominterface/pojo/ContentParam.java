package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class ContentParam<T> implements Serializable {

    private T value;

    private int commandType;

    private int from;

    public ContentParam(T value, int commandType, int from) {
        this.value = value;
        this.commandType = commandType;
        this.from = from;
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentParam<?> that = (ContentParam<?>) o;
        return commandType == that.commandType &&
                from == that.from &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, commandType, from);
    }

    @Override
    public String toString() {
        return "ContentParam{" +
                "value=" + value +
                ", commandType=" + commandType +
                ", from=" + from +
                '}';
    }
}
