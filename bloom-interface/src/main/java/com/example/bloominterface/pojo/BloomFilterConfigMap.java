package com.example.bloominterface.pojo;

import java.io.Serializable;
import java.util.Objects;

public class BloomFilterConfigMap  implements Serializable {

    private String expectedQuantityStr;

    private String misjudgmentRateStr;


    public BloomFilterConfigMap() {
    }

    public BloomFilterConfigMap(String expectedQuantityStr, String misjudgmentRateStr) {
        this.expectedQuantityStr = expectedQuantityStr;
        this.misjudgmentRateStr = misjudgmentRateStr;
    }

    public String getExpectedQuantityStr() {
        return expectedQuantityStr;
    }

    public void setExpectedQuantityStr(String expectedQuantityStr) {
        this.expectedQuantityStr = expectedQuantityStr;
    }

    public String getMisjudgmentRateStr() {
        return misjudgmentRateStr;
    }

    public void setMisjudgmentRateStr(String misjudgmentRateStr) {
        this.misjudgmentRateStr = misjudgmentRateStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloomFilterConfigMap that = (BloomFilterConfigMap) o;
        return Objects.equals(expectedQuantityStr, that.expectedQuantityStr) &&
                Objects.equals(misjudgmentRateStr, that.misjudgmentRateStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedQuantityStr, misjudgmentRateStr);
    }

    @Override
    public String toString() {
        return "BloomFilterConfigMap{" +
                "expectedQuantityStr='" + expectedQuantityStr + '\'' +
                ", misjudgmentRateStr='" + misjudgmentRateStr + '\'' +
                '}';
    }
}
