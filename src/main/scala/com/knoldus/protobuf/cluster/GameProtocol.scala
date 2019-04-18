package com.knoldus.protobuf.cluster

import akka.actor.ActorRef

trait MarkerTrait

case class GameMessage(msg: String, ref: ActorRef) extends MarkerTrait

case class GameSuccess(msg : String) extends MarkerTrait

object GameProtocol {}
