package com.example.bloomserver.excutor;

import com.example.bloominterface.pojo.ClientCommandRes;
import com.example.bloominterface.pojo.LogEntry;

public interface EnventExcutor {

    public ClientCommandRes writeIn(LogEntry logEntry);

    public ClientCommandRes read(Object paramter);

    public ClientCommandRes getServerDeatil(Object paramter);



}
