name := """haruBackend"""
organization := "com.haru"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.20.3"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.haru.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.haru.binders._"