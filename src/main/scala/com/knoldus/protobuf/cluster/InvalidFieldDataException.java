package com.knoldus.protobuf.cluster;

class InvalidFieldDataException extends RuntimeException {
    public InvalidFieldDataException(String message, Exception cause) {
        super(message, cause);
    }
}

class TransformerUtilityException extends RuntimeException {
    public TransformerUtilityException(String message, Exception cause){
        super(message, cause);
    }
}