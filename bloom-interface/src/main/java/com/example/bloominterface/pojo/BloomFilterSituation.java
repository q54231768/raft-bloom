package com.example.bloominterface.pojo;


import java.io.Serializable;
import java.util.Objects;

//布隆过滤器状态的POJO类
public class BloomFilterSituation implements Serializable {

    //布隆过滤器容器大小
    private int bitSize;

    //布隆过滤器哈希函数个数
    private int hashNum = 8;


    //插入次数
    private double insertCount;

    //冲突次数
    private double collideCount;

    //当前碰撞率
    private double collisionProbability;

    public BloomFilterSituation() {

    }

    public BloomFilterSituation(int bitSize, int hashNum, double insertCount, double collideCount, double collisionProbability) {
        this.bitSize = bitSize;
        this.hashNum = hashNum;
        this.insertCount = insertCount;
        this.collideCount = collideCount;
        this.collisionProbability = collisionProbability;
    }

    public int getBitSize() {
        return bitSize;
    }

    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }

    public int getHashNum() {
        return hashNum;
    }

    public void setHashNum(int hashNum) {
        this.hashNum = hashNum;
    }

    public double getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(double insertCount) {
        this.insertCount = insertCount;
    }

    public double getCollideCount() {
        return collideCount;
    }

    public void setCollideCount(double collideCount) {
        this.collideCount = collideCount;
    }

    public double getCollisionProbability() {
        return collisionProbability;
    }

    public void setCollisionProbability(double collisionProbability) {
        this.collisionProbability = collisionProbability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloomFilterSituation that = (BloomFilterSituation) o;
        return bitSize == that.bitSize &&
                hashNum == that.hashNum &&
                Double.compare(that.insertCount, insertCount) == 0 &&
                Double.compare(that.collideCount, collideCount) == 0 &&
                Double.compare(that.collisionProbability, collisionProbability) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize, hashNum, insertCount, collideCount, collisionProbability);
    }

    @Override
    public String toString() {
        return "BloomFilterSituation{" +
                "bitSize=" + bitSize +
                ", hashNum=" + hashNum +
                ", insertCount=" + insertCount +
                ", collideCount=" + collideCount +
                ", collisionProbability=" + collisionProbability +
                '}';
    }
}
