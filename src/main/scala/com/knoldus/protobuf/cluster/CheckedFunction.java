package com.knoldus.protobuf.cluster;

@FunctionalInterface
public interface CheckedFunction<R, T> {
    R apply(T t) throws Exception;
}
