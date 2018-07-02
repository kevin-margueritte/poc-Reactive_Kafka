name := "poc-reactive-kafka"

version := "1.0"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.13"

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"         % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"       % akkaVersion,
  "org.scalatest"     %% "scalatest"          % "3.0.5"       % "test",
  "com.typesafe.akka" %% "akka-stream-kafka"  % "0.22"
)

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core"        % circeVersion,
  "io.circe" %% "circe-generic"     % circeVersion,
  "io.circe" %% "circe-parser"      % circeVersion
)

libraryDependencies ++= Seq(
  "net.debasishg"             %% "redisclient" % "3.7",
  "com.softwaremill.macwire"  %% "macros"      % "2.3.0" % Provided
)