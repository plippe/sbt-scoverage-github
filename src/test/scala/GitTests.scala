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

        'testGitGetRemoteUrlOrigin - {
            assert(Git.getRemoteUrl("origin").isRight)
        }

        'testGitGetRemoteUrlBad - {
            assert(Git.getRemoteUrl("bad").isLeft)
        }

        'testGitGetHeadSha - {
            assert(Git.getHeadSha().isRight)
        }

    }
}
