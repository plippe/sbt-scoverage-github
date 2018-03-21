package com.github.plippe

import cats._
import cats.effect._
import cats.implicits._
import java.io.File
import java.util.NoSuchElementException
import org.http4s.client.blaze.Http1Client
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import com.github.plippe.github._

object Main {
    def main(args: Array[String]): Unit = {
        val namedFiles = Map(
            "A" -> new File("/Users/pvinchon/Code/sbt-scoverage-github/src/test/resources/scoverage.a.xml"),
            "B" -> new File("/Users/pvinchon/Code/sbt-scoverage-github/src/test/resources/scoverage.b.xml"),
            "C" -> new File("/Users/pvinchon/Code/sbt-scoverage-github/src/test/resources/scoverage.empty.xml")
        )

        val gitHubToken = System.getenv("GITHUB_TOKEN")

        run[IO](namedFiles, gitHubToken).unsafeRunSync
    }

    def run[F[_]](namedFiles: Map[String, File], gitHubToken: String)(implicit ec: ExecutionContext, M: MonadError[F, Throwable], E: Effect[F]): F[Boolean] = {
        for {
            namedCoverages <- namedFiles.toList
                .map { case (n, f) => ScoverageXmlReader.read(f).map { c => NamedCoverage(n, c) } }
                .traverse(M.fromEither)

            report = NamedCoverages.render(namedCoverages.toArray, Comment.maxWidth)
            comment = Comment(s"```\n$report\n```")

            gitRemoteUrl <- M.fromEither(Git.remoteUrl("origin"))
            gitHeadSha <- M.fromEither(Git.headSha)

            http4sClient <- Http1Client[F]()
            httpClient = HttpClientHttp4s[F](http4sClient)
            gitHubClient =  GitHubClientCirce[F](httpClient, gitHubToken)

            gitHubPullRequests <- gitHubClient.listPullRequests(gitRemoteUrl.owner, gitRemoteUrl.repository)
            gitHubPullRequest <- gitHubPullRequests.find(_.head.sha == gitHeadSha)
                .fold(M.raiseError[PullRequest](new NoSuchElementException(s"No pull request with head sha $gitHeadSha")))(M.pure)

            _ <- gitHubClient.postComment(gitRemoteUrl.owner, gitRemoteUrl.repository, gitHubPullRequest.number, comment)
        } yield true
    }
}
