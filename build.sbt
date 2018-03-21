sbtPlugin := true

organization := "com.github.plippe"
name := "sbt-scoverage-github"

publishMavenStyle := false
bintrayRepository := "sbt"
bintrayOrganization in bintray := None

enablePlugins(GitVersioning)

scalaVersion := "2.12.4"
crossSbtVersions := Vector("0.13.17", "1.1.1")

val circeVersion = "0.9.1"
val http4sVersion = "0.18.3"
val utestVersion = "0.6.3"
val sbtSCoverageVersion = "1.5.1"

libraryDependencies ++= Seq(
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "com.lihaoyi" %% "utest" % utestVersion % Test,
    Defaults.sbtPluginExtra(
        "org.scoverage" % "sbt-scoverage" % sbtSCoverageVersion,
        (sbtBinaryVersion in pluginCrossBuild).value,
        (scalaBinaryVersion in pluginCrossBuild).value
    ))

testFrameworks += new TestFramework("utest.runner.Framework")
