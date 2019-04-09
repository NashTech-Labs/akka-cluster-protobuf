package com.knoldus.protobuf.cluster

import akka.actor.{ActorRef, ExtendedActorSystem}
import akka.protobuf.ByteString
import akka.remote.WireFormats.ActorRefData
import akka.remote.serialization.ProtobufSerializer
import akka.serialization.BaseSerializer
import com.google.protobuf
import com.knoldus.protobuf.models.example.{ProtoGameMessage, Success}

class CustomBaseSerialzer(val system: ExtendedActorSystem) extends BaseSerializer
{
    final val GameMessageManifest = classOf[GameMessage].getName
    final val SuccessManifest = classOf[Success].getName

    override def toBinary(o : AnyRef) : Array[Byte] = {
        o match {
            case game : GameMessage => {
                println(s">>>>>>>>>>>>>>>>>>> Serialize $game Message <<<<<<<<<<<<<<<<<<<< ")
                val refData : ActorRefData = ProtobufSerializer.serializeActorRef(game.ref)
                val newMessage = ProtoGameMessage(game.msg, protobuf.ByteString.copyFrom(refData.toByteString.toByteArray))
                newMessage.toByteArray
            }
            case success : Success => {
                success.toByteArray
            }
        }
    }

    override def includeManifest : Boolean = true

    override def fromBinary(bytes : Array[Byte], manifest : Option[Class[_]]) : AnyRef = {
        manifest match {
            case Some(clazz) => {
                println(s">>>>>>>>>>>>>>>>>>> De-Serialize ${clazz.getName} Message <<<<<<<<<<<<<<<<<<<< ")
                if(clazz.getName == GameMessageManifest){
                    val message : ProtoGameMessage = ProtoGameMessage.parseFrom(bytes)
                    val refData = ActorRefData.parseFrom(ByteString.copyFrom(message.ref.toByteArray))
                    val ref = ProtobufSerializer.deserializeActorRef(system, refData)
                    GameMessage(message.id, ref)
                }else if(clazz.getName == SuccessManifest){
                    Success.parseFrom(bytes)
                } else {
                    throw new ClassNotFoundException("invalid manifest name")
                }
            }
            case None => throw new IllegalArgumentException("Invalid class")
        }
    }
}
