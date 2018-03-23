# sbt-scoverage-github

[![Build Status](https://app.wercker.com/status/8771cc6d453d68d69b7f700810a7eb21/s/master)](https://app.wercker.com/project/byKey/8771cc6d453d68d69b7f700810a7eb21)
[![Version](https://api.bintray.com/packages/plippe/sbt/sbt-scoverage-github/images/download.svg)](https://bintray.com/plippe/sbt/sbt-scoverage-github/_latestVersion)

Sbt plugin to summarize [scoverage](http://scoverage.org/) reports, and post it on github pull request as a comment.
The plugin uses the git remote url to find the repository, and the commit sha for the pull request.

The plugin requires a valid GitHub access token. Once can be created in [your settings](https://github.com/settings/tokens).


### Installing sbt-scoverage-github

Add the plugin to `project/plugins.sbt` file.

```sbt
// in project/plugins.sbt
resolvers += Resolver.url("plippe-sbt", url("http://dl.bintray.com/plippe/sbt"))(Resolver.ivyStylePatterns)
addSbtPlugin("com.github.plippe" % "sbt-scoverage-github" % "XXX")
```

Enable the plugin in your `build.sbt` file, and configure it.

```sbt
// in build.sbt
enablePlugins(ScoverageGithubPlugin)

gitHubRemoteName := "" // Default is origin
gitHubToken := Some("") // Default is the content of the environment variable GITHUB_TOKEN
scoverageReports := Map(
    "PR" -> new File("./target/scala-2.12/scoverage-report/scoverage.xml"),
    "MASTER" -> new FILE("...")
)
```


### Using sbt-scoverage-github

To read the given scoverage reports, and post the summary, run the `scoverageGitHubPost` task.

```bash
sbt scoverageGitHubPost
```

For a continous integration, install [sbt-scoverage](https://github.com/scoverage/sbt-scoverage), and adapt the
following to your project.

```bash
# Download scoverage master report, save it where sbt-scoverage-github will read it
curl [SCOVERAGE_MASTER_BRANCH] > ./target/scala-2.12/scoverage-report/master.scoverage.xml

# Generate report for current state
sbt clean coverage test
sbt coverageReport

# Post summary to GitHub
sbt scoverageGitHubPost
```
