package com.github.plippe

import sys.process._
import scala.util.Try

case class RemoteUrl(url: String, domain: String, owner: String, repo: String)

object RemoteUrl {

    def fromString(str: String): Either[Throwable, RemoteUrl] = {
        val httpsUrl = raw"https://([^/]*)/([^/]*)/([^/]*).git".r
        val sshUrl = raw"([^@]*)@([^:]*):([^/]*)/([^/]*).git".r

        str.toLowerCase.trim match {
            case httpsUrl(domain, owner, repo) => Right(RemoteUrl(str, domain, owner, repo))
            case sshUrl(user, domain, owner, repo) => Right(RemoteUrl(str, domain, owner, repo))
            case _ => Left(new RuntimeException(s"String not a valid git url: $str"))
        }
    }

}

case object Git {

    def getRemoteUrl(remoteName: String): Either[Throwable, RemoteUrl] = {
        val bash = s"git config --get remote.$remoteName.url"

        for {
            remoteUrlString <- Try(bash !!).toEither
            remoteUrl <- RemoteUrl.fromString(remoteUrlString)
        } yield {
            remoteUrl
        }
    }

}
