package com.knoldus.protobuf.cluster

import akka.actor.ActorRef

trait ProtobufSerializable

case class GameMessage(msg: String, ref: ActorRef) extends ProtobufSerializable

case class GameSuccess(msg : String) extends ProtobufSerializable

object GameProtocol {}
