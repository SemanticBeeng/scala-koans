package org.functionalkoans.forscala

/**
  *
  */
class AboutFunctors {

  trait Functor[F[_]] {
    self =>
    /** Lift `f` into `F` and apply to `F[A]`. */
    def map[A, B](fa: F[A])(f: A => B): F[B]

  }

}
