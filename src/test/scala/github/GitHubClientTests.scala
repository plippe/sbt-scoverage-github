package com.github.plippe.github

import cats.Eval
import cats.data.EitherT
import cats.implicits._
import utest._

import com.github.plippe.HttpClient

object GitHubClientTests extends TestSuite {
    type E[T] = EitherT[Eval, Throwable, T]

    val defaultHttpClient = new HttpClient[E] {
        override def get[O](uriString: String, headers: Map[String, String], decode: String => E[O]): E[O] = ???
        override def post[I, O](uriString: String, headers: Map[String, String], body: I, encode: I => String, decode: String => E[O]): E[O] = ???
    }

    val tests = Tests {

        'testHeaders - {
            val client = GitHubClientCirce[E](defaultHttpClient, "abc")
            assert(client.headers == Map("Authorization" -> "token abc"))
        }

    }
}
