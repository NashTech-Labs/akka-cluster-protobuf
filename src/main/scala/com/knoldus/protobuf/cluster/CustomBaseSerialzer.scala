package com.knoldus.protobuf.cluster

import akka.actor.ExtendedActorSystem
import akka.protobuf.ByteString
import akka.remote.WireFormats.ActorRefData
import akka.remote.serialization.ProtobufSerializer
import akka.serialization.BaseSerializer
import com.google.protobuf
import com.knoldus.protobuf.models.example.ProtoGameMessage

class CustomBaseSerialzer(val system: ExtendedActorSystem) extends BaseSerializer
{
    final val GameMessageManifest = classOf[GameMessage].getName

    override def toBinary(o : AnyRef) : Array[Byte] = {
        o match {
            case game : GameMessage => {
                val refData : ActorRefData = ProtobufSerializer.serializeActorRef(game.ref)
                val newMessage = ProtoGameMessage(game.msg, protobuf.ByteString.copyFrom(refData.toByteString.toByteArray))
                newMessage.toByteArray
            }
        }
    }

    override def includeManifest : Boolean = true

    override def fromBinary(bytes : Array[Byte], manifest : Option[Class[_]]) : AnyRef = {
        manifest match {
            case Some(clazz) => {
                if(clazz.getName == GameMessageManifest){
                    val message : ProtoGameMessage = ProtoGameMessage.parseFrom(bytes)
                    val refData = ActorRefData.parseFrom(ByteString.copyFrom(message.ref.toByteArray))
                    ProtobufSerializer.deserializeActorRef(system, refData)
                } else {
                    throw new ClassNotFoundException("invalid manifest name")
                }
            }
            case None => throw new IllegalArgumentException("Invalid class")
        }
    }
}
