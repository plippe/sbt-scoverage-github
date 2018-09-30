sbtPlugin := true

organization := "com.github.plippe"
name := "sbt-scoverage-github"

publishMavenStyle := false
bintrayRepository := "sbt"
bintrayOrganization in bintray := None

enablePlugins(GitVersioning)
git.useGitDescribe := true

scalaVersion := "2.12.4"
crossSbtVersions := Vector("0.13.17", "1.1.1")

val github4sVersion = "0.15.0" // java.lang.AbstractMethodError for anything above
val utestVersion = "0.6.3"
val scoverageVersion = "1.3.1"

libraryDependencies ++= Seq(
    "org.scoverage" %% "scalac-scoverage-plugin" % scoverageVersion,
    "com.47deg" %% "github4s" % github4sVersion,
    "com.lihaoyi" %% "utest" % utestVersion % Test
)

testFrameworks += new TestFramework("utest.runner.Framework")
