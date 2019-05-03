package com.knoldus.protobuf.cluster.exception

trait ExceptionMarkerTrait {
    private var exception : Option[APIExceptionProto] = None

    def initCause(apiException: APIExceptionProto) = {
        exception = Some(apiException)
    }
}
