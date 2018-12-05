package com.github.plippe.implicits

import scala.util.{Try, Success, Failure}

trait TryImplicits {
  implicit class TrySyntax[A](value: Try[A]) {
    def toEither(): Either[Throwable, A] = value match {
      case Success(a)   => Right(a)
      case Failure(err) => Left(err)
    }
  }
}
