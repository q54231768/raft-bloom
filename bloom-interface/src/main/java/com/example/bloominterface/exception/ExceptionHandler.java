package com.example.bloominterface.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ExceptionHandler {
    public void handle(Exception e, Logger logger);
}
