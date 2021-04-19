package com.example.bloominterface.exception;

import com.example.bloominterface.exception.impl.DefaultExceptionHandler;
import org.slf4j.Logger;

public class GlobalLoggerHandler {

    public static ExceptionHandler  exceptionHandler = new DefaultExceptionHandler();

    public static void handle(Exception e, Logger logger){
        exceptionHandler.handle(e, logger);
    }

}
