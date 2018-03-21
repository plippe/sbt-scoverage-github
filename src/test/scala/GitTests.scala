package com.github.plippe

import utest._

object GitTests extends TestSuite {
    val tests = Tests {

        'testRemoteUrlFromHttps - {
            val url = "https://github.com/plippe/sbt-scoverage-github.git"
            assert(RemoteUrl.fromString(url) == Right(RemoteUrl(url, "github.com", "plippe", "sbt-scoverage-github")))
        }

        'testRemoteUrlFromSsh - {
            val url = "git@github.com:plippe/sbt-scoverage-github.git"
            assert(RemoteUrl.fromString(url) == Right(RemoteUrl(url, "github.com", "plippe", "sbt-scoverage-github")))
        }

        'testRemoteUrlFromBad - {
            val url = "not a valid url"
            assert(RemoteUrl.fromString(url).isLeft)
        }

        'testGitRemoteUrlOrigin - {
            assert(Git.remoteUrl("origin").isRight)
        }

        'testGitRemoteUrlBad - {
            assert(Git.remoteUrl("bad").isLeft)
        }

        'testGitHeadSha - {
            assert(Git.headSha().isRight)
        }

    }
}
