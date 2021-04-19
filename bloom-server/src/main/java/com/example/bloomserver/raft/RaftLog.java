package com.example.bloomserver.raft;

import com.alibaba.fastjson.JSON;
import com.example.bloominterface.exception.ExceptionHandler;
import com.example.bloominterface.exception.GlobalLoggerHandler;
import com.example.bloominterface.pojo.LogEntry;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RaftLog {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RaftLog.class);
    private static final Logger LOGGER = null;

    //日志数据库文件位置
    private String logDir;

    private RocksDB rocksDB;

    //日志数据库中的最后一个日志号
    private final static byte[] LAST_INDEX_KEY = "LAST_INDEX_KEY".getBytes();

    //日志数据库中提交到状态机的索引号
    private final static byte[] APPLY_INDEX_KEY = "APPLY_INDEX_KEY".getBytes();


    //日志锁,用于保证日志更新的同步
    private ReentrantLock logLock = new ReentrantLock();



    static {
        RocksDB.loadLibrary();
    }


    public RaftLog() {
    }

    public RaftLog(String logDir) {
        this.logDir = logDir;
        Options options = new Options();
        options.setCreateIfMissing(true);
        File file = new File(logDir);
        boolean fileExists = false;
        if (!file.exists()) {
            fileExists = false;
        }
        if (fileExists) {
            LOGGER.info("创建日志目录" + logDir);
        }
        try {
            this.rocksDB = RocksDB.open(options, logDir);
        } catch (RocksDBException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public RaftLog(String logDir, ExceptionHandler exceptionHandler) {
        this.logDir = logDir;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public RocksDB getRocksDB() {
        return rocksDB;
    }

    public void setRocksDB(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }

    public static byte[] getLastIndexKey() {
        return LAST_INDEX_KEY;
    }




    public long getLastIndex() throws RocksDBException {
        byte[] result = this.rocksDB.get(LAST_INDEX_KEY);
        if (result == null) {
            return -1;
        } else {
            String lastIndexStr = new String(result);
            long lastIndex = Long.parseLong(lastIndexStr);
            return lastIndex;
        }
    }


    public boolean writeLog(LogEntry logEntry) {
        boolean result = false;
        try {
            this.logLock.tryLock(2000, TimeUnit.MILLISECONDS);
            long index = getLastIndex();
            ++index;
            logEntry.setIndex(index);
            String indexStr = index + "";
            String logJson = JSON.toJSONString(logEntry);
            this.rocksDB.put(indexStr.getBytes(), logJson.getBytes());
            updateLastIndex(index);
            result = true;
        } catch (RocksDBException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            logLock.unlock();
        }
        return result;
    }

    public long checkLogEntrys(LogEntry[] logEntrys){
        if(logEntrys == null || logEntrys.length == 0) return -2;
        long index = logEntrys[0].getIndex();
        try {
            this.logLock.tryLock(3000, TimeUnit.MILLISECONDS);
            long lastIndex = getLastIndex();
            for (int i = 0; i < logEntrys.length && index <= lastIndex; ++i) {
                LogEntry newLogEntry = getLog(index);
                if(newLogEntry == null) break;
                if(newLogEntry.getIndex() != logEntrys[i].getIndex()
                        || newLogEntry.getTerm() != logEntrys[i].getTerm()) break;
                ++ index;
            }
        } catch (RocksDBException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            logLock.unlock();
        }
        return index;
    }




    public boolean batchWriteLog(LogEntry[] logEntrys) {
        if (logEntrys == null || logEntrys.length == 0) {
            return true;
        }
        boolean result = false;
        try {
            this.logLock.tryLock(3000, TimeUnit.MILLISECONDS);
            long index = logEntrys[0].getIndex() - 1;
            for (int i = 0; i < logEntrys.length; ++i) {
                ++index;
                logEntrys[i].setIndex(index);
                String indexStr = index + "";
                String logJson = JSON.toJSONString(logEntrys[i]);
                this.rocksDB.put(indexStr.getBytes(), logJson.getBytes());
            }
            updateLastIndex(index);
            result = true;
        } catch (RocksDBException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            logLock.unlock();
        }
        return result;
    }


    //删除从start索引之后的所有日志
    public boolean batchDeleteLog(long start) {
        boolean result = false;
        try {
            this.logLock.tryLock(300, TimeUnit.MILLISECONDS);
            long lastIndex = getLastIndex();
            if(start <= lastIndex) {
                    updateLastIndex(start - 1);
                    for(long i = start;start < lastIndex + 1; ++start){
                        rocksDB.delete((i + "").getBytes());
                    }
                    result = true;
            }
        } catch (RocksDBException | InterruptedException e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        } finally {
            logLock.unlock();
        }
        return result;

    }


    public LogEntry getLog(long index) throws RocksDBException {
        String indexStr = index + "";
        byte[] bytes = rocksDB.get(indexStr.getBytes());
        if (bytes == null) {
            return null;
        }
        String json = new String(bytes);
        LogEntry logEntry = JSON.parseObject(json, LogEntry.class);
        return logEntry;
    }


    public void updateLastIndex(long lastIndex) throws RocksDBException {
        String lastIndexStr = lastIndex + "";
        this.rocksDB.put(LAST_INDEX_KEY, lastIndexStr.getBytes());
    }


//    public long getApplyIndex() throws RocksDBException {
//        byte[] bytes = rocksDB.get(APPLY_INDEX_KEY);
//        if (bytes == null) {
//            return -1;
//        } else {
//            String applyIndexStr = new String(bytes);
//            long applyIndex = Long.parseLong(applyIndexStr);
//            return applyIndex;
//        }
//    }


}
