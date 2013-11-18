name := "blackpepper"

organization := "com.whisk"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalariformSettings

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.2.0",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.0-rc1",
  "org.apache.cassandra" % "cassandra-all" % "2.0.2" % "test",
  "org.specs2" %% "specs2-core" % "2.3.4" % "test")
