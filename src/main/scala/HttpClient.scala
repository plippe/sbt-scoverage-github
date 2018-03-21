package com.github.plippe

import cats.MonadError
import cats.effect.Sync
import cats.syntax.all._
import org.http4s.{ Header, Headers, Request, Uri }
import org.http4s.client.{ Client => Http4sClient }
import org.http4s.client.dsl.Http4sClientDsl._
import org.http4s.dsl.io._

trait HttpClient[F[_]] {
    def get[O](uriString: String, headers: Map[String, String], decode: String => F[O]): F[O]
    def post[I, O](uriString: String, headers: Map[String, String], body: I, encode: I => String, decode: String => F[O]): F[O]
}

case class HttpClientHttp4s[F[_]](client: Http4sClient[F])(implicit M: MonadError[F, Throwable], sync: Sync[F]) extends HttpClient[F] {

    def emptyUri = uri("/")

    def get[O](uriString: String, headers: Map[String, String], decode: String => F[O]): F[O] = {
        val req = GET(emptyUri)
        request(uriString, headers, req, decode)
    }

    def post[I, O](uriString: String, headers: Map[String, String], body: I, encode: I => String, decode: String => F[O]): F[O] = {
        val req = POST(emptyUri, encode(body))
        request(uriString, headers, req, decode)
    }

    def request[O](uriString: String, headers: Map[String, String], fReq: F[Request[F]], decode: String => F[O]): F[O] = {
        val http4sHeaders = headers.map { case (name, value) => Header(name, value) }.toList

        for {
            uri <- Uri.fromString(uriString).fold(M.raiseError, M.pure)
            req <- fReq.map(_.withUri(uri).withHeaders(Headers(http4sHeaders)))
            enc <- client.expect[String](req)
            res <- decode(enc)
        } yield res
    }

}
