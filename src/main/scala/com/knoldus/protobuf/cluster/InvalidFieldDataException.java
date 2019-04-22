package com.knoldus.protobuf.cluster;

public class InvalidFieldDataException extends RuntimeException {
    public InvalidFieldDataException(String message, Exception cause) {
        super(message, cause);
    }
}
