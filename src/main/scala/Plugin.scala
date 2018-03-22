import sbt._, Keys._

import cats.data.NonEmptyList
import cats.effect.{ IO => CIO }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

import com.github.plippe.Main

trait ScoverageGithubKeys {

  lazy val gitHubToken = settingKey[Option[String]]("GitHub personnal access token, https://github.com/settings/tokens")
  lazy val scoverageReports = settingKey[List[(String, File)]]("Scoverage reports to compare")

  lazy val scoverageGitHubPost = taskKey[Unit]("Post scoverage report differences to open GitHub pull request")

}

object ScoverageGithubPlugin extends AutoPlugin {

  override lazy val  projectSettings: Seq[Setting[_]] = scoverageGithubProjectSettings

  object autoImport extends ScoverageGithubKeys
  import autoImport._

  def scoverageGithubProjectSettings: Seq[Setting[_]] = Seq(
    gitHubToken := Properties.envOrNone("GITHUB_TOKEN"),
    scoverageGitHubPost := post.value,
  )

  def post = Def.task {
    val optNamedFiles = NonEmptyList.fromList(scoverageReports.value)
    val optGitHubToken = gitHubToken.value

    val result = (optNamedFiles, optGitHubToken) match {
        case (None, _) => CIO.raiseError(new IllegalArgumentException(s"No scoverage reports given"))
        case (_, None) => CIO.raiseError(new NoSuchElementException(s"No GitHub token found"))
        case (Some(namedFiles), Some(gitHubToken)) => Main.run[CIO](namedFiles, gitHubToken)
    }

    result.unsafeRunSync
  }

}
