package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class PingPong extends Actor with ActorLogging {

    override def receive : Receive = {
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages) if msg.toLowerCase == "ping" =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("Pong", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages)
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages) if msg.toLowerCase == "ding" =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("Dong", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages)
        case GameMessage(msg, ref, status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages) =>
            log.info(s"\n ========================= $msg =======================")
            ref ! GameReply("""¯\_(ツ)_/¯""", status, optionRef, stage, currentLevel, optionCurrentLevel, regionType, levels, levelsV, stages)
        case msg =>
            log.info("""\n ( ͡° ʖ̯ ͡°) _________ (ง’̀-‘́)ง _________ (▀̿Ĺ̯▀̿ ̿)""", msg)
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


