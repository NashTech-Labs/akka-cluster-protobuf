package com.knoldus.protobuf.cluster

import akka.actor.ActorRef
import com.knoldus.protobuf.cluster.RegionType.RegionType

trait ProtobufSerializable

object RegionType extends Enumeration
{
    type RegionType = Value

    val AWS_NORTH_VIRGINIA = Value(0, "us-east-1")
    val AWS_OHIO = Value(1, "us-east-2")
    val AWS_NORTH_CALIFORNIA = Value(2, "us-west-1")
    val AWS_OREGON = Value(3, "us-west-2")
    val AWS_CANADA = Value(4, "ca-central-1")
    val AWS_IRELAND = Value(5, "eu-west-1")
    val AWS_FRANFURKT = Value(6, "eu-central-1")
    val AWS_LONDON = Value(7, "eu-west-2")
    val AWS_SINGAPORE = Value(8, "ap-southeast-1")
    val AWS_SYDNEY = Value(9, "ap-southeast-2")
    val AWS_SEOUL = Value(10, "ap-northeast-2")
    val AWS_TOKYO = Value(11, "ap-northeast-1")
    val AWS_MUMBAI = Value(12, "ap-south-1")
    val AWS_SAO_PAOLO = Value(13, "sa-east-1")
    val LOCAL_IL_1 = Value(14, "il-1")
}

case class Level(number : Int) extends ProtobufSerializable

case class Stage(level : Level) extends ProtobufSerializable

case class GameMessage(
    msg : String,
    ref : ActorRef,
    status : Option[Boolean],
    optionRef : Option[ActorRef],
    stage : Stage,
    currentLevel : Int,
    optionCurrentLevel : Option[Level],
    regionType: RegionType
) extends ProtobufSerializable

case class GameSuccess(
    msg : String,
    status : Option[Boolean],
    optionRef : Option[ActorRef],
    stage : Stage,
    currentLevel : Int,
    optionCurrentLevel : Option[Level],
    regionType: RegionType
) extends ProtobufSerializable

object GameProtocol
{}
