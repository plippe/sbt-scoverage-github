package com.github.plippe

import cats.Eval
import cats.data.EitherT
import cats.implicits._
import org.http4s.{ HttpService, ParseFailure, Request, Response }
import org.http4s.client.{ Client => Http4s }
import org.http4s.dsl.io._
import utest._

object HttpClientTests extends TestSuite {
    type E[T] = EitherT[Eval, Throwable, T]
    def right[T](value: T): E[T] = EitherT.right[Throwable](Eval.now(value))
    def left[T](value: Throwable): E[T] = EitherT.left[T](Eval.now(value))

    val defaultUri = "/"
    val defaultHeaders = Map.empty[String, String]
    val defaultService = HttpService[E] { case r => Response[E](Ok).withBodyStream(r.body).pure[E] }
    val defaultDecoder = { res: String => right(()) }

    def request[T](
        uri: String = defaultUri,
        headers: Map[String, String] = defaultHeaders,
        decoder: String => E[T] = defaultDecoder,
        service: HttpService[E] = defaultService
    ): E[T] = {

        val mockHttp4s = Http4s.fromHttpService(service)
        val client = HttpClientHttp4s[E](mockHttp4s)

        val req = right(Request.apply[E]())

        client.request(uri, headers, req, decoder)

    }

    val tests = Tests {

        'testRequestBadUri - {
            val result = request(uri = "not a valid uri")

            assert(result.isLeft.value)
            assert(result.swap.exists(_.isInstanceOf[ParseFailure]).value)
        }

        'testRequestChangesUri - {
            val service = HttpService[E] {
                case r if r.uri == uri("/good") => Response[E](Ok).pure[E]
                case r => Response[E](BadRequest).pure[E]
            }

            val result = request(uri = "/good", service = service)
            assert(result.isRight.value)
        }

        'testRequestAddsHeader - {
            val service = HttpService[E] {
                case r if r.headers.exists(h => h.name.value == "a" && h.value == "b") => Response[E](Ok).pure[E]
                case r => Response[E](BadRequest).pure[E]
            }

            val result = request(headers = Map("a" -> "b"), service = service)
            assert(result.isRight.value)
        }

        'testRequestDecodesResponse - {
            val result = request(decoder = { _ => right("good") })

            assert(result.isRight.value)
            assert(result.exists(_ == "good").value)
        }

        'testGetMethod - {
            val service = HttpService[E] {
                case r if r.method == GET => Response[E](Ok).pure[E]
                case r => Response[E](BadRequest).pure[E]
            }

            val mockHttp4s = Http4s.fromHttpService(service)
            val client = HttpClientHttp4s[E](mockHttp4s)

            val result = client.get("", defaultHeaders, defaultDecoder)

            assert(result.isRight.value)
        }

        'testPostMethod - {
            val service = HttpService[E] {
                case r if r.method == POST => Response[E](Ok).pure[E]
                case r => Response[E](BadRequest).pure[E]
            }

            val mockHttp4s = Http4s.fromHttpService(service)
            val client = HttpClientHttp4s[E](mockHttp4s)

            val result = client.post("", defaultHeaders, "", { req: String => "" }, defaultDecoder)

            assert(result.isRight.value)
        }

        'testPostEncoder - {
            val mockHttp4s = Http4s.fromHttpService(defaultService)
            val client = HttpClientHttp4s[E](mockHttp4s)

            def encoder(body: String) = {
                assert(body == "body")
                "new-body"
            }

            def decoder(body: String) = {
                assert(body == "new-body")
                right("newer-body")
            }

            val result = client.post("", defaultHeaders, "body", encoder, decoder)

            assert(result.isRight.value)
            assert(result.exists(_ == "newer-body").value)
        }

    }
}
