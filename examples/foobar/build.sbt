enablePlugins(ScoverageGithubPlugin)
scoverageReports := Map(
    "current state" -> new File("./target/scala-2.12/scoverage-report/scoverage.xml")
)
