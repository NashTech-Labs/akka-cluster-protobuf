/*
package com.knoldus.protobuf.cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class SimpleClusterListener extends Actor with ActorLogging
{

    val cluster = Cluster(context.system)

    override def preStart() : Unit = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    }

    override def postStop() : Unit = cluster.unsubscribe(self)


    override def receive : Receive = {
        case MemberUp(member) =>
             log.info("Member us Uo: {}", member.address)
        case UnreachableMember(member) =>
            log.info("Member detected as unreachable: {}", member)
        case MemberRemoved(member, previousStatus) =>
            log.info("Member is removed: {} after {}", member.address, previousStatus)
        case _: MemberEvent =>
    }


}
*/
