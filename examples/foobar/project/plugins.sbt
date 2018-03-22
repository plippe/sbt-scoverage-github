// build root project
lazy val root = Project("plugins", file(".")) dependsOn(scoverageGitHub)

// depends on the scoverageGitHub project
lazy val scoverageGitHub = RootProject(file("../../.."))
