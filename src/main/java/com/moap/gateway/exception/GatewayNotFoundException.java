package com.moap.gateway.exception;

public class GatewayNotFoundException extends RuntimeException {
    public GatewayNotFoundException(String msg) {
        super(msg);
    }
}
