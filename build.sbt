name := "blackpepper"

organization := "com.whisk"

val gitHeadCommitSha = settingKey[String]("current git commit SHA")

val paradiseVersion = "2.0.1"

gitHeadCommitSha in ThisBuild := Process("git rev-parse --short HEAD").lines.head

version in ThisBuild := "0.1.0-" + gitHeadCommitSha.value

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.11.4", "2.10.4")

scalariformSettings

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8", "-deprecation", "-unchecked", "-feature", "-language:_")

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.7",
  "com.typesafe.play" %% "play-iteratees" % "2.3.7",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.3",
  "org.apache.cassandra" % "cassandra-all" % "2.0.10" % "test",
  "org.specs2" %% "specs2-core" % "2.3.11" % "test")

libraryDependencies ++= (
  if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
  else Nil)

publishTo := {
  val dir = if (version.value.trim.endsWith(gitHeadCommitSha.value)) "snapshots" else "releases"
  val repo = Path.userHome / "mvn-repo" / dir
  Some(Resolver.file("file", repo) )
}
