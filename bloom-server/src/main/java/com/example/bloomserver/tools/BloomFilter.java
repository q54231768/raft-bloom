package com.example.bloomserver.tools;


import com.example.bloomserver.tools.hash.BKDRHash;
import com.example.bloomserver.tools.hash.HashFunction;
import com.example.bloomserver.tools.pojo.BloomFilterSituation;

public class BloomFilter {

    //存储位图
    private BitSet bitSet;

    //布隆过滤器容器大小
    private int bitSize;

    //布隆过滤器哈希函数个数
    private int hashNum = 8;

    //布隆过滤器哈希函数
    private HashFunction hashFunctions[];

    //插入次数
    private double insertCount;

    //冲突次数
    private double collideCount;

    //当前碰撞率
    private double collisionProbability;


    public BloomFilter(int expectedQuantity, double misjudgmentRate) {
        Double res = new Double(0 - (expectedQuantity * Math.log(misjudgmentRate)) / (Math.log(2) * Math.log(2)));
        this.bitSize = res.intValue() + 1;
        this.hashNum = new Double((Math.log(2) * this.bitSize) / expectedQuantity).intValue() + 1;
        this.bitSet = new BitSet(this.bitSize);
        this.hashFunctions = getHashFunction(hashNum);

        System.out.println(this.bitSize);
        System.out.println(this.hashNum);
    }


    public BloomFilter(BloomFilterSituation bloomFilterSituation, long words[]) {
        this.bitSize = bloomFilterSituation.getBitSize();
        this.hashNum = bloomFilterSituation.getHashNum();
        this.insertCount = bloomFilterSituation.getInsertCount();
        this.collideCount = bloomFilterSituation.getCollideCount();
        this.collisionProbability = bloomFilterSituation.getCollisionProbability();
        this.hashFunctions = getHashFunction(hashNum);
        bitSet = new BitSet(this.bitSize, words);
        System.out.println(this.bitSize);
        System.out.println(this.hashNum);


    }


    /**
     * 插入元素
     *
     * @param
     */
    public void put(String content) {
        synchronized (this) {
            ++insertCount;
        }
        if (this.mightContain(content)) {
            synchronized (this) {
                ++collideCount;
                collisionProbability = collideCount / insertCount;
            }
            return;
        }
        for (int i = 0; i < this.hashFunctions.length; ++i) {
            int hash = this.hashFunctions[i].hash(content);
            hash = hash % bitSize;
            bitSet.set(hash);
        }
    }


    /**
     * 检查元素是否在布隆过滤器内，只能判断一定不在不能判断一定在
     *
     * @param
     */
    public boolean mightContain(String content) {
        for (int i = 0; i < this.hashFunctions.length; ++i) {
            int hash = this.hashFunctions[i].hash(content);
            hash = hash % bitSize;
            if (!this.bitSet.get(hash)) return false;
        }
        return true;
    }


    public void reset(int expectedQuantity, double misjudgmentRate){
        Double res = new Double(0 - (expectedQuantity * Math.log(misjudgmentRate)) / (Math.log(2) * Math.log(2)));
        this.bitSize = res.intValue() + 1;
        this.hashNum = new Double((Math.log(2) * this.bitSize) / expectedQuantity).intValue() + 1;
        this.bitSet = new BitSet(this.bitSize);
        this.hashFunctions = getHashFunction(hashNum);
        this.insertCount=0;
        this.collideCount=0;
        this.collisionProbability=0;
    }

    public int getHashNum() {
        return hashNum;
    }

    public HashFunction[] getHashFunctions() {
        return hashFunctions;
    }

    public void setHashFunctions(HashFunction[] hashFunctions) {
        this.hashFunctions = hashFunctions;
    }


    /**
     * 生成count个哈希函数
     *
     * @param
     */
    public HashFunction[] getHashFunction(int count) {
        HashFunction[] hashFunctions = new HashFunction[count];
        int seeds[] = makeSeeds(count);
        for (int i = 0; i < count; ++i) {
            hashFunctions[i] = new BKDRHash(seeds[i]);
        }
        return hashFunctions;
    }


    public double getInsertCount() {
        return insertCount;
    }

    public double getCollideCount() {
        return collideCount;
    }

    public double getCollisionProbability() {
        return collisionProbability;
    }

    public BitSet getBitSet() {
        return bitSet;
    }

    public void setBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public int getBitSize() {
        return bitSize;
    }

    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }

    public void setHashNum(int hashNum) {
        this.hashNum = hashNum;
    }

    public void setInsertCount(double insertCount) {
        this.insertCount = insertCount;
    }

    public void setCollisionProbability(double collisionProbability) {
        this.collisionProbability = collisionProbability;
    }

    public void setCollideCount(double collideCount) {
        this.collideCount = collideCount;
    }

    public BloomFilterSituation getBloomFilterSituation() {
        return new BloomFilterSituation(this.bitSize,
                this.hashNum,
                this.insertCount,
                this.collideCount,
                this.collisionProbability
        );
    }




    /**
     * 生成num个哈希种子数,哈希种子数为大于127的质数
     *
     * @param
     */
    public int[] makeSeeds(int count) {
        if (count <= 0) count = 8;
        if (count > 50) count = 50;
        int seeds[] = new int[count];
        int pos = 0;
        boolean[] N=new boolean[1000];
        N[0]=N[1]=true;//0和1都为非素数
        for (int i = 2; i<999 ; i++) {//从2开始筛选
            if(!N[i]){
                for (int j = 2*i; j < 999; j+=i) {
                    N[j]=true;
                }
            }
        }
        //循环退出后，素数为false非素数为true
        //也可以在创建数组时对数组进行初始化
        for (int i = 160,j=0; i < 999 && j<count; i++) {
            if(!N[i]){
                seeds[j]=i;
                ++j;
            }
        }
        getSeeds(seeds);
        return seeds;
    }


    public int[] getSeeds(int seeds[]) {
        for (int i = 0; i < seeds.length; ++i) {
            System.out.print(seeds[i] + " ");
        }
        System.out.println(" ");
        return seeds;
    }

}
