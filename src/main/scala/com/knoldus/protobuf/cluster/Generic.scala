package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorRef, ActorSystem, ExtendedActorSystem, Props}
import com.google.protobuf.ByteString
import com.knoldus.protobuf.cluster.ReflectionJavaUtility.createInstanceOfProtoClassFromClass

trait SomeTrait {
    def toByteArray: Array[Byte]

    def parseFrom(s: Array[Byte]): AnyRef
}

case class Message(name: String, id: Int, ref : Option[ActorRef], contact : Option[String])

case class MessageProto(name: String, id: Int, ref : Option[ByteString], contact : Option[String])

object MessageProto extends SomeTrait {
    def toByteArray: Array[Byte] = {
        Array(0, 1, 2, 3, 4, 5, 6, 7)
    }

    def parseFrom(s : Array[Byte]) : AnyRef = {
        val byteArray = Array[Byte](10, 49, 97, 107, 107, 97, 46, 116, 99, 112, 58, 47, 47, 84, 101, 115, 116,
            64, 49, 50, 55, 46, 48, 46, 48, 46, 49, 58, 51, 56, 54, 49, 57, 47, 117, 115, 101, 114, 47, 36, 97, 35,
            57, 48, 50, 48, 57, 57, 51, 49, 54)
        MessageProto("James", 30, Some(ByteString.copyFrom(byteArray)), Some("Moga"))
    }
}

class TestActor extends Actor {
    override def receive : Receive = {
        case _ => println("Message received successfully")
    }
}

object Generic extends App {

    val system = ActorSystem("Test")
    val ref = system.actorOf(Props(classOf[TestActor]))
    val message = Message("James", 30, Some(ref), Some("Moga"))

    val anyRef : AnyRef = createInstanceOfProtoClassFromClass(message.getClass.getName, message.getClass, message)
    println("Mapped object : "+ anyRef + " ========= " + anyRef.getClass)

    val value = ReflectionScalaUtility.invokeToByteArrayMethod(anyRef.getClass)
    println(" >>>>>>>>>>>>>>>>>>>>>>> " + value.mkString(","))

    val data = ReflectionScalaUtility.invokeParseFromMethod(anyRef.getClass, value);
    println(" *************************** " + data.getClass)

    val classObject = ReflectionJavaUtility.createInstanceOfClassFromProtoClass(data.getClass.getName, data.getClass, data, system.asInstanceOf[ExtendedActorSystem])
    println("Mapped object : "+ classObject + " @@@@@@@@@ " + classObject.getClass)
}
