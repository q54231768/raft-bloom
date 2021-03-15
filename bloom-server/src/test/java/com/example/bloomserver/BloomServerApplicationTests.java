package com.example.bloomserver;

import com.example.bloominterface.pojo.LogEntry;
import com.example.bloomserver.ThreadPool.RaftThreadPool;
import com.example.bloomserver.raft.RaftLog;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@SpringBootTest
class BloomServerApplicationTests {

//    private Logger logger= LoggerFactory.getLogger(this.toString());
//
//    @Test
//    void contextLoads() {
//    }
//
    @Test
    public void testWriteLog(){

        RaftLog raftLog = new RaftLog("D:\\raft-log\\1");
        LogEntry logEntry = LogEntry.newBuilder().
                term(1).
                commandType(1).
                index(2).
                value("infra").
                build();
        raftLog.writeLog(logEntry);
    }

    @Test
    public void getLog() throws RocksDBException {
        RaftLog raftLog = new RaftLog("D:\\raft-log\\1");
        System.out.println(raftLog.getLastIndex());
        System.out.println(raftLog.getLog(3));
        System.out.println(raftLog.getLog(0));
    }

    @Test
    public void testBatchWriteLog(){
        RaftLog raftLog = new RaftLog("D:\\raft-log\\1");
        for(int i=1;i<3;++i) {
            LogEntry logEntry = LogEntry.newBuilder().
                    term(i).
                    commandType(i).
                    index(i).
                    value("infra").
                    build();
            raftLog.writeLog(logEntry);

        }



    }


    @Test
    public void testCreateFile(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File("D:\\28134518\\3.txt");
                    if(file.createNewFile()){
                        System.out.println("yes");
                    }else{
                        System.out.println("no");
                    }
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write("hello".getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }


    @Test
    public void testFutureTaskSubmit(){
        ArrayList<Future> arrayList = new ArrayList<>(20);
        AtomicInteger integer = new AtomicInteger(0);
        System.out.println(integer.get());
        System.out.println(" ");
        RaftThreadPool raftThreadPool = new RaftThreadPool();
        raftThreadPool.init();
        ArrayList<Future> arr = new ArrayList<>(20);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for(int i=0;i<20;++i){
            int finalI = i;
            arr.add(raftThreadPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    sleep(50);
                    integer.incrementAndGet();
                    countDownLatch.countDown();
                    return finalI;
                }
            }));
        }
        try {
            countDownLatch.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for (int i=0;i<20;++i){
//            try {
//                System.out.println((Integer) arr.get(i).get());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
        System.out.println(" ");
        System.out.println(integer.get());
    }




}
