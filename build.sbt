sbtPlugin := true

version := "0.0.1-SNAPSHOT"

organization := "com.seanstoppable"

name := "sbt-apidoc-generator"

libraryDependencies ++= Seq(
  "com.bryzek.apidoc" %% "apidoc-api" % "0.1-SNAPSHOT",
  "com.bryzek.apidoc" %% "apidoc-core" % "0.1-SNAPSHOT",
  "com.bryzek.apidoc" %% "apidocgenerator-scalagenerator" % "0.1-SNAPSHOT",
  "com.typesafe.play" %% "play-json" % "2.4.2"
)
