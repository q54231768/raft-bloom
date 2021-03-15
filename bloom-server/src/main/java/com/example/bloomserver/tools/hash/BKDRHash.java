package com.example.bloomserver.tools.hash;



public class BKDRHash implements HashFunction {

    private int seed = 131;

    public BKDRHash() {
    }

    public BKDRHash(int seed) {

        this.seed = seed;
    }


    @Override
    public int hash(String content) {
        int hash = 0;
        for (int i = 0; i < content.length(); ++i) {
            hash = hash * this.seed + content.charAt(i);
        }
        return hash & Integer.MAX_VALUE;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
