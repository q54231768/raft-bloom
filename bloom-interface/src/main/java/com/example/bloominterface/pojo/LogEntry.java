package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class LogEntry implements Serializable,Comparable {

    private long index = -1;

    private long term;

    private int commandType;

    private String value;


    public static Builder newBuilder() {
        return new Builder();
    }

    public  LogEntry(){

    }

    public LogEntry(long index, long term, int commandType, String value) {
        this.index = index;
        this.term = term;
        this.commandType = commandType;
        this.value = value;
    }

    public  LogEntry(Builder builder){
        setIndex(builder.index);
        setTerm(builder.term);
        setCommandType(builder.commandType);
        setValue(builder.value);
    }


    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry logEntry = (LogEntry) o;
        return index == logEntry.index &&
                term == logEntry.term &&
                commandType == logEntry.commandType &&
                Objects.equals(value, logEntry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, term, commandType, value);
    }


    @Override
    public String toString() {
        return "LogEntry{" +
                "index=" + index +
                ", term=" + term +
                ", commandType=" + commandType +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        if (this.getIndex() > ((LogEntry) o).getIndex()) {
            return 1;
        }
        return -1;
    }

    public static final class Builder{

        private long index;

        private long term;

        private int commandType;

        private String value;


        public Builder() {
        }

        public Builder index(long index){
            this.index = index;
            return this;
        }

        public Builder term(long term){
            this.term = term;
            return this;
        }


        public Builder commandType(int commandType){
            this.commandType = commandType;
            return this;
        }

        public Builder value(String value){
            this.value = value;
            return this;
        }


        public LogEntry build(){
            return new LogEntry(this);
        }
    }

}
