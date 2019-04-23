package com.knoldus.protobuf.cluster

import akka.actor.ActorRef

trait ProtobufSerializable

case class Level(number : Int) extends ProtobufSerializable

case class Stage(level : Level) extends ProtobufSerializable

case class GameMessage(msg : String, ref : ActorRef, status : Option[Boolean], optionRef : Option[ActorRef], stage : Stage) extends ProtobufSerializable

case class GameSuccess(msg : String, status : Option[Boolean], optionRef : Option[ActorRef], stage : Stage) extends ProtobufSerializable

object GameProtocol
{}
