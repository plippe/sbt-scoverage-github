package com.github.plippe

import sys.process._
import scala.util.Try

case class RemoteUrl(url: String, domain: String, owner: String, repository: String)

object RemoteUrl {

    def fromString(str: String): Either[Throwable, RemoteUrl] = {
        val httpsUrl = raw"https://([^/]*)/([^/]*)/([^/]*).git".r
        val sshUrl = raw"([^@]*)@([^:]*):([^/]*)/([^/]*).git".r

        str.toLowerCase match {
            case httpsUrl(domain, owner, repo) => Right(RemoteUrl(str, domain, owner, repo))
            case sshUrl(user, domain, owner, repo) => Right(RemoteUrl(str, domain, owner, repo))
            case _ => Left(new RuntimeException(s"String not a valid git url: $str"))
        }
    }

}

case object Git {

    def remoteUrl(remoteName: String): Either[Throwable, RemoteUrl] = {
        val bash = s"git config --get remote.$remoteName.url"

        for {
            remoteUrlString <- Try(bash !!).toEither.map(_.trim)
            remoteUrl <- RemoteUrl.fromString(remoteUrlString)
        } yield {
            remoteUrl
        }
    }

    def headSha(): Either[Throwable, String] = {
        val bash = s"git rev-parse HEAD"
        Try(bash !!).toEither.map(_.trim)
    }

}
