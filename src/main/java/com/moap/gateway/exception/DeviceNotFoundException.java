package com.moap.gateway.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String msg) {
        super(msg);
    }
}
