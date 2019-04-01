package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.knoldus.protobuf.cluster.Game.Success
import com.typesafe.config.ConfigFactory

class PingPong extends Actor with ActorLogging {

    override def receive : Receive = {
        case GameMessage(msg) =>
            log.info(s"\n ========================= GameMessage($msg) =======================")
            sender() ! Success
    }
}

object PingPong{

    def main(args : Array[String]) : Unit = {
        val port = if (args.isEmpty) "0" else args(0)

        val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
            .withFallback(ConfigFactory.parseString("akka.cluster.roles = [pingpong]"))
            .withFallback(ConfigFactory.load("application.conf"))

        val system = ActorSystem("GameZone", config)
        system.actorOf(Props(classOf[PingPong]), name = "PingPong")
    }

}

case class GameMessage(msg: String)
