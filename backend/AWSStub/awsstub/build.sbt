name := """AWSStub"""
organization := "tom.ff.awsstub"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  "net.logstash.logback" % "logstash-logback-encoder" % "6.2",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "io.lemonlabs" %% "scala-uri" % "1.5.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "tom.ff.awsstub.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "tom.ff.awsstub.binders._"
