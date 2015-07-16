name := "blackpepper"

organization := "com.whisk"

val gitHeadCommitSha = settingKey[String]("current git commit SHA")

gitHeadCommitSha in ThisBuild := Process("git rev-parse --short HEAD").lines.head

version in ThisBuild := "0.1.0-" + gitHeadCommitSha.value

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

parallelExecution in Test := false

fork in Test := true

scalariformSettings

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-language:_")

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.2",
  "com.typesafe.play" %% "play-iteratees" % "2.4.2",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5",
  "org.apache.cassandra" % "cassandra-all" % "2.0.10" % "test",
  "org.specs2" %% "specs2-core" % "2.3.11" % "test")

publishTo := {
  val dir = if (version.value.trim.endsWith(gitHeadCommitSha.value)) "snapshots" else "releases"
  val repo = Path.userHome / "mvn-repo" / dir
  Some(Resolver.file("file", repo) )
}
