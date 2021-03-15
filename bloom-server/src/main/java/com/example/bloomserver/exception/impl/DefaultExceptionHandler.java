package com.example.bloomserver.exception.impl;


import com.example.bloomserver.exception.ExceptionHandler;

import org.slf4j.Logger;

public class DefaultExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(Exception e, Logger logger) {
        e.printStackTrace();
        logger.warn(e.getMessage(), e);
    }

}
