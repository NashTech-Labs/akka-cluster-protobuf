package com.knoldus.protobuf.cluster

object Main extends App {
    println(">>>>>>>>>>>>>>>> Load on Port 9891 <<<<<<<<<<<<<<<<<")
    PingPong.main(Array("9891"))
    println(">>>>>>>>>>>>>>>> Load on Port 9892 <<<<<<<<<<<<<<<<<")
    PingPong.main(Array("9892"))
    println(">>>>>>>>>>>>>>>> Initialize Game Launcher <<<<<<<<<<<<<<<<<")
    Game.main(Array.empty)
}
