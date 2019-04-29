package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

class Game extends Actor with ActorLogging {

    val pingPong = context.actorOf(FromConfig.props(), name = "PingPongRouter")

    override def receive : Receive = {
        case msg : GameReply =>
            log.info(s"\n ========================= $msg =======================")
    }

    override def preStart() : Unit = {
        log.info("\n >>>>>>>>>>>>> Bang from the GameLauncher after 10 Seconds <<<<<<<<<<<<<<<<<" + pingPong.path)
        Thread.sleep(10000)
        log.info("\n ----------------- About createInstanceOfProtoClassFromClass bang -----------------------")
        pingPong ! GameMessage(
            msg = "Pong",
            ref = self,
            status = Some(true),
            optionRef = Some(self),
            stage = Stage(Level(1)),
            currentLevel = 1,
            optionCurrentLevel = Some(Level(1)),
            regionType = RegionType.AWS_LONDON,
            levels = List(1, 2, 3, 4, 5),
            levelsV = Vector(21, 22, 23, 24, 25),
            stages = Seq(Stage(Level(61)), Stage(Level(63)), Stage(Level(62)))
        )

        pingPong ! GameMessage(
            msg = "Ding",
            ref = self,
            status = Some(false),
            optionRef = Some(self),
            stage = Stage(Level(5)),
            currentLevel = 5,
            optionCurrentLevel = Some(Level(5)),
            regionType = RegionType.AWS_MUMBAI,
            levels = List(11, 22, 33, 44, 55),
            levelsV = Vector(58, 65, 15, 32, 65),
            stages = Seq(Stage(Level(258)), Stage(Level(369)), Stage(Level(147)))
        )

        pingPong ! GameMessage(
            msg = "Buddy",
            ref = self,
            status = Some(false),
            optionRef = Some(self),
            stage = Stage(Level(25)),
            currentLevel = 25,
            optionCurrentLevel = Some(Level(25)),
            regionType = RegionType.AWS_SAO_PAOLO,
            levels = List(34, 45, 67, 89, 23),
            levelsV = Vector(78, 56, 78, 34, 3258),
            stages = Seq(Stage(Level(758)), Stage(Level(68)), Stage(Level(275)))
        )
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
