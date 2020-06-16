name := """haruBackend"""
organization := "com.haru"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.reactivemongo" % "play2-reactivemongo_2.12" % "0.20.3-play28",
  "org.reactivemongo" % "reactivemongo-play-json_2.12" % "0.20.3-play28",
  "org.reactivemongo" % "reactivemongo-bson-macros_2.12" % "0.20.3-noshaded",
  "org.joda" % "joda-convert" % "2.2.1",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.2",
  "io.lemonlabs" %% "scala-uri" % "1.5.1",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "org.mockito" %% "mockito-scala-scalaz" % "1.11.3"
)

scalacOptions += "-feature"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.haru.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.haru.binders._"
