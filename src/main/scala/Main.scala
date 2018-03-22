package com.github.plippe

import cats._
import github4s.Github
import github4s.Github._
import github4s.free.domain.PRFilterOpen
import github4s.jvm.Implicits._
import java.io.File
import java.util.NoSuchElementException
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties
import scalaj.http.HttpResponse

object Main {
    def main(args: Array[String]): Unit = {
        val namedFiles = Map(
            "A" -> new File("src/main/resources/scoverage.a.xml"),
            "B" -> new File("src/main/resources/scoverage.b.xml"),
            "C" -> new File("src/main/resources/scoverage.empty.xml")
        )

        val gitRemoteName = "origin"

        val optGitHubToken = Properties.envOrNone("GITHUB_TOKEN")

        val result = run(namedFiles, gitRemoteName, optGitHubToken)
        result match {
            case Right(succ) => println("Success")
            case Left(err) => throw err
        }
    }

    def flatten[A, E](list: List[Either[E, A]]): Either[E, List[A]] = {
        val initial: Either[E, List[A]] = Right(List.empty)

        list.foldLeft(initial) {
            case (Right(list), Right(el)) => Right(list :+ el)
            case (err@Left(_), _) => err
            case (_, Left(err)) => Left(err)
        }
    }

    def run(namedFiles: Map[String, File], gitRemoteName: String, optGitHubToken: Option[String]): Either[Throwable, Boolean] = {
        val gitHubCommentMaxWidth = 88

        for {
            coverages <- flatten(namedFiles.values.map(ScoverageXmlReader.read).toList)
            namedCoverages = namedFiles.keys.zip(coverages).map { case (n, c) => NamedCoverage(n, c) }

            report = NamedCoverages.render(namedCoverages.toArray, gitHubCommentMaxWidth)
            reportAsCode = s"```\n$report\n```"

            gitRemoteUrl <- Git.remoteUrl(gitRemoteName)
            gitHeadSha <- Git.headSha

            gitHubClient = Github(optGitHubToken)

            gitHubPullRequests <- gitHubClient.pullRequests.list(gitRemoteUrl.owner, gitRemoteUrl.repository, List(PRFilterOpen))
                .exec[Id, HttpResponse[String]]()
            gitHubPullRequest <- gitHubPullRequests.result.find(_.head.exists(_.sha == gitHeadSha))
                .toRight(new NoSuchElementException(s"No pull request with head sha $gitHeadSha"))

            _ <- gitHubClient.issues.createComment(gitRemoteUrl.owner, gitRemoteUrl.repository, gitHubPullRequest.number, reportAsCode)
                .exec[Id, HttpResponse[String]]()
        } yield true
    }
}
