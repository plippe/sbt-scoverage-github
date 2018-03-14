sbtPlugin := true

organization := "com.github.plippe"
name := "sbt-scoverage-github"

publishMavenStyle := false
bintrayRepository := "sbt"
bintrayOrganization in bintray := None

enablePlugins(GitVersioning)

scalaVersion := "2.12.4"
crossSbtVersions := Vector("0.13.17", "1.1.1")

libraryDependencies ++= Seq(
    "com.lihaoyi" %% "utest" % "0.6.3" % "test",
    Defaults.sbtPluginExtra(
        "org.scoverage" % "sbt-scoverage" % "1.5.1",
        (sbtBinaryVersion in pluginCrossBuild).value,
        (scalaBinaryVersion in pluginCrossBuild).value
    ))

testFrameworks += new TestFramework("utest.runner.Framework")
