package com.example.bloomserver.exception;

import org.slf4j.Logger;

public interface ExceptionHandler {
    public void handle(Exception e, Logger logger);
}
