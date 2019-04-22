package com.knoldus.protobuf.cluster

import akka.actor.ActorRef

trait ProtobufSerializable

case class GameMessage(msg: String, ref: ActorRef, status : Option[Boolean], optionRef : Option[ActorRef]) extends ProtobufSerializable

case class GameSuccess(msg : String, status : Option[Boolean], optionRef : Option[ActorRef]) extends ProtobufSerializable

object GameProtocol {}
