name := """sandbox-server"""
organization := "com.waffelmonster"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

resolvers += "Zomis' Maven Repository" at "http://www.zomis.net/maven/"

libraryDependencies += guice
libraryDependencies += evolutions
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.32.3.2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "net.zomis" % "mine-analyze" % "1.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.waffelmonster.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.waffelmonster.binders._"
