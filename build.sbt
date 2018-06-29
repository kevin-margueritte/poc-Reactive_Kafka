name := "poc-reactive-kafka"

version := "1.0"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"         % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"       % akkaVersion,
  "org.scalatest"     %% "scalatest"          % "3.0.5"       % "test",
  "com.typesafe.akka" %% "akka-stream-kafka"  % "0.20"
)

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core"        % circeVersion,
  "io.circe" %% "circe-generic"     % circeVersion,
  "io.circe" %% "circe-parser"      % circeVersion,
  "io.circe" %% "circe-derivation"  % "0.9.0-M3"
)

libraryDependencies ++= Seq(
  "net.debasishg"             %% "redisclient" % "3.7",
  "com.softwaremill.macwire"  %% "macros"      % "2.3.0" % Provided,
  "org.typelevel"             %% "cats-effect" % "1.0.0-RC2"
)