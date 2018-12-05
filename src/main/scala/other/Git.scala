package com.github.plippe

import sys.process._
import scala.util.Try

import com.github.plippe.implicits._
trait Scala212_Git { avoidUnusedImport() }

case object Git {

    def remoteUrl(remoteName: String): Either[Throwable, String] = {
        val bash = s"git config --get remote.$remoteName.url"
        for {
            remoteUrlString <- Try(bash !!).toEither.map(_.trim)
        } yield {
            remoteUrlString
        }
    }

    def headSha(): Either[Throwable, String] = {
        val bash = s"git rev-parse HEAD"
        Try(bash !!).toEither.map(_.trim)
    }

}

case class GitHubRemoteUrl(owner: String, repository: String)

object GitHubRemoteUrl {

    def fromString(str: String): Either[Throwable, GitHubRemoteUrl] = {
        val httpUrl = raw"https?://github.com/(.+)/([^.]+).*".r
        val sshUrl = raw"git@github.com:(.+)/(.+).git".r

        str.toLowerCase match {
            case httpUrl(owner, repo) => Right(GitHubRemoteUrl(owner, repo))
            case sshUrl(owner, repo) => Right(GitHubRemoteUrl(owner, repo))
            case _ => Left(new RuntimeException(s"String not a valid GitHub url: $str"))
        }
    }

}
