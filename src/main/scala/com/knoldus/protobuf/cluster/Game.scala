package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

class Game extends Actor with ActorLogging {

    val pingPong = context.actorOf(FromConfig.props(), name = "PingPongRouter")

    override def receive : Receive = {
        case msg : GameSuccess =>
            log.info(s"\n ========================= $msg =======================")
    }

    override def preStart() : Unit = {
        log.info("\n >>>>>>>>>>>>> Bang from the GameLauncher after 10 Seconds <<<<<<<<<<<<<<<<<" + pingPong.path)
        Thread.sleep(10000)
        log.info("\n ----------------- About createInstanceOfProtoClassFromClass bang -----------------------")
//        pingPong ! GameMessage("Ping", self, None)
        pingPong ! GameMessage("Pong", self, Some(true), Some(self), Stage(Level(1)))
    }
}

object Game {
    def main(args: Array[String]): Unit = {
        val config = ConfigFactory.parseString("akka.cluster.roles = [game]")
        .withFallback(ConfigFactory.load("application.conf"))

        val system = ActorSystem("GameZone", config)
        system.log.info("Game will start when 2 PingPong members in the cluster")

        Cluster(system) registerOnMemberUp {
            system.actorOf(Props(classOf[Game]), name = "GameLauncher")
        }

        Cluster(system).registerOnMemberRemoved {
            system.registerOnTermination(System.exit(0))
            system.terminate()
        }
    }
}
