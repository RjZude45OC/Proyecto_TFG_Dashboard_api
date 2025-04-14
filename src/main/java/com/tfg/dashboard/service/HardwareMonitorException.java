package com.tfg.dashboard.service;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HardwareMonitorException extends RuntimeException {

    private final HttpStatus status;

    public HardwareMonitorException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HardwareMonitorException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
