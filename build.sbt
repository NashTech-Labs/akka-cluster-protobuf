name := "akkacluster-with-protobuf"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.21"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "org.scala-lang" % "scala-reflect" % "2.12.8"
)

PB.protocVersion := "-v351"


PB.targets in Compile := Seq(
    scalapb.gen() -> (sourceManaged in Compile).value
)

fork in run := true

mainClass in(Compile, run) := Some("com.knoldus.protobuf.cluster.Main")