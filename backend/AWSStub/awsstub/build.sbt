name := """AWSStub"""
organization := "tom.ff.awsstub"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "1.5.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "tom.ff.awsstub.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "tom.ff.awsstub.binders._"
