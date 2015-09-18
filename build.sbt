name := "blackpepper"

organization := "com.whisk"

val gitHeadCommitSha = settingKey[String]("current git commit SHA")

gitHeadCommitSha in ThisBuild := Process("git rev-parse --short HEAD").lines.head

version in ThisBuild := "0.2.1"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization := Some("whisk")

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

parallelExecution in Test := false

fork in Test := true

scalariformSettings

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "com.typesafe.play" %% "play-iteratees" % "2.4.3",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.7.1",
  "org.apache.cassandra" % "cassandra-all" % "2.0.10" % "test",
  "org.specs2" %% "specs2-core" % "2.3.11" % "test")

bintrayRepository := {
  if (version.value.trim.endsWith(gitHeadCommitSha.value)) "maven-snapshots" else "maven"
}
