package com.knoldus.protobuf.cluster

import akka.actor.Status.Success
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class PingPong extends Actor with ActorLogging {

    override def receive : Receive = {
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards) if msg.toLowerCase == "ping" =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("Pong", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards)
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards) if msg.toLowerCase == "ding" =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("Dong", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards)
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards) =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("""¯\_(ツ)_/¯""", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages, rewards)
        case status @ Success(msg) =>
            log.info(s"\n ========================= $msg =======================")
            sender() ! status
        case msg @ Some =>
            log.info(s"\n ========================= $msg =======================")
            sender() ! msg
        case None =>
            log.info(s"\n ========================= None =======================")
            sender() ! None
        case msg =>
            log.info(
                """
                  |( ͡° ʖ̯ ͡°) _________ {} _________ (▀̿Ĺ̯▀̿ ̿) """.stripMargin, msg)

            sender() ! msg
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


