package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.google.protobuf.ByteString
import com.knoldus.protobuf.cluster.ReflectionJavaUtility.createInstanceOfProtoClassFromClass

trait SomeTrait {
    def toByteArray: Array[Byte]
}

case class Message(name: String, id: Int, ref : Option[ActorRef], contact : Option[String])

case class MessageProto(name: String, id: Int, ref : Option[ByteString], contact : Option[String])

object MessageProto extends SomeTrait {
    def toByteArray: Array[Byte] = {
        Array(0, 1, 2, 3, 4, 5, 6, 7)
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

    val value = ReflectionScalaUtility.invokeToByteArrayMethod(anyRef.getClass, anyRef)
    println(" >>>>>>>>>>>>>>>>>>>>>>> " + value)
}
