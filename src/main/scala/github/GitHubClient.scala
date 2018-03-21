package com.github.plippe.github

import cats.MonadError
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.parser.decode

import com.github.plippe.HttpClient

trait GitHubClient[F[_]] {

    def httpClient: HttpClient[F]
    def token: String

    def headers = Map("Authorization" -> s"token $token")

    def pullRequestsUri(owner: String, repository: String) = s"https://api.github.com/repos/$owner/$repository/pulls"
    def commentUri(owner: String, repository: String, number: Int) = s"https://api.github.com/repos/$owner/$repository/issues/$number/comments"

    def decodeListPullRequests(json: String): F[Iterable[PullRequest]]
    def listPullRequests(owner: String, repository: String): F[Iterable[PullRequest]] = {
        val uri = pullRequestsUri(owner, repository)
        httpClient.get[Iterable[PullRequest]](uri, headers, decodeListPullRequests)
    }

    def encodePostComment(comment: Comment): String
    def decodePostComment(json: String): F[Unit]
    def postComment(owner: String, repository: String, number: Int, comment: Comment): F[Unit] = {
        val uri = commentUri(owner, repository, number)
        httpClient.post(uri, headers, comment, encodePostComment, decodePostComment)
    }

}

case class GitHubClientCirce[F[_]](httpClient: HttpClient[F], token: String)(implicit M: MonadError[F, Throwable]) extends GitHubClient[F] {

    override def decodeListPullRequests(json: String): F[Iterable[PullRequest]] = {
        decode[Iterable[PullRequest]](json).fold(M.raiseError, M.pure)
    }

    override def encodePostComment(comment: Comment): String = comment.asJson.noSpaces
    override def decodePostComment(json: String): F[Unit] = M.unit

}
