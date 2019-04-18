package com.knoldus.protobuf.cluster

import akka.actor.ExtendedActorSystem
import akka.protobuf.ByteString
import akka.remote.WireFormats.ActorRefData
import akka.remote.serialization.ProtobufSerializer
import akka.serialization.BaseSerializer
import com.knoldus.protobuf.cluster.ReflectionJavaUtility.createInstanceOfProtoClassFromClass

class CustomBaseSerialzer(val system: ExtendedActorSystem) extends BaseSerializer
{
    final val GameMessageManifest = classOf[GameMessage].getName
    final val SuccessManifest = classOf[GameSuccessProto].getName

    override def toBinary(o : AnyRef) : Array[Byte] = {
        o match {
            case message : MarkerTrait => {
                println(s">>>>>>>>>>>>>>>>>>> Serialize $message Message <<<<<<<<<<<<<<<<<<<< ")
                val anyRef : AnyRef = createInstanceOfProtoClassFromClass(message.getClass.getName, message.getClass, message)
                ReflectionScalaUtility.invokeToByteArrayMethod(anyRef.getClass, anyRef)
                /*val refData : ActorRefData = ProtobufSerializer.serializeActorRef(game.ref)
                val newMessage = ProtoGameMessage(game.msg, protobuf.ByteString.copyFrom(refData.toByteString.toByteArray))
                newMessage.toByteArray*/
            }
            case _ => Array.empty
        }
    }

    override def includeManifest : Boolean = true

    override def fromBinary(bytes : Array[Byte], manifest : Option[Class[_]]) : AnyRef = {
        manifest match {
            case Some(clazz) => {
                println(s">>>>>>>>>>>>>>>>>>> De-Serialize ${clazz.getName} Message <<<<<<<<<<<<<<<<<<<< ")
                if(clazz.getName == GameMessageManifest){
                    val message : GameMessageProto = GameMessageProto.parseFrom(bytes)
                    val refData = ActorRefData.parseFrom(ByteString.copyFrom(message.ref.toByteArray))
                    val ref = ProtobufSerializer.deserializeActorRef(system, refData)
                    GameMessage(message.id, ref)
                }else if(clazz.getName == SuccessManifest){
                    GameSuccessProto.parseFrom(bytes)
                } else {
                    throw new ClassNotFoundException("invalid manifest name")
                }
            }
            case None => throw new IllegalArgumentException("Invalid class")
        }
    }
}
