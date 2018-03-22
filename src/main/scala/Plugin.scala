import sbt._, Keys._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

import com.github.plippe.Main

trait ScoverageGithubKeys {

  lazy val gitHubRemoteName = settingKey[String]("GitHub remote name")
  lazy val gitHubToken = settingKey[Option[String]]("GitHub personnal access token, https://github.com/settings/tokens")
  lazy val scoverageReports = settingKey[Map[String, File]]("Scoverage reports to compare")

  lazy val scoverageGitHubPost = taskKey[Unit]("Post scoverage report differences to open GitHub pull request")

}

object ScoverageGithubPlugin extends AutoPlugin {

  override lazy val  projectSettings: Seq[Setting[_]] = scoverageGithubProjectSettings

  object autoImport extends ScoverageGithubKeys
  import autoImport._

  def scoverageGithubProjectSettings: Seq[Setting[_]] = Seq(
    gitHubRemoteName := "origin",
    gitHubToken := Properties.envOrNone("GITHUB_TOKEN"),
    scoverageGitHubPost := post.value,
  )

  def post = Def.task {
    val result = Main.run(scoverageReports.value, gitHubRemoteName.value, gitHubToken.value)
    result match {
      case Right(succ) => streams.value.log.info("success")
      case Left(err) => throw err
    }
  }

}
