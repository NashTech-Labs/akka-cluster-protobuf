name := "akkacluster-with-protobuf"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.21"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
)

fork in run := true

mainClass in(Compile, run) := Some("com.knoldus.protobuf.cluster.Main")