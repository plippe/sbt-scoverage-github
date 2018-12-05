package com.github.plippe.implicits

trait EitherImplicits {
  implicit class EitherSyntax[A, B](value: Either[A, B]) {
    def map[C](f: B => C): Either[A, C] =
      value match {
        case Right(b) => Right(f(b))
        case Left(a)  => Left(a)
      }

    def flatMap[AA >: A, C](f: B => Either[AA, C]): Either[AA, C] =
      value match {
        case Right(b) => f(b)
        case Left(a)  => Left(a)
      }
  }
}
