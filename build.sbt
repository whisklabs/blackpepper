name := "blackpepper"

organization := "com.whisk"

val gitHeadCommitSha = settingKey[String]("current git commit SHA")

gitHeadCommitSha in ThisBuild := Process("git rev-parse --short HEAD").lines.head

version in ThisBuild := "0.1.0-" + gitHeadCommitSha.value

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.11.0", "2.10.4")

scalariformSettings

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-language:_")

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.0-RC1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.2",
  "org.apache.cassandra" % "cassandra-all" % "2.0.2" % "test",
  "org.specs2" %% "specs2-core" % "2.3.11" % "test")

publishTo := {
  val dir = if (version.value.trim.endsWith(gitHeadCommitSha.value)) "snapshots" else "releases"
  val repo = Path.userHome / "mvn-repo" / dir
  Some(Resolver.file("file", repo) )
}
