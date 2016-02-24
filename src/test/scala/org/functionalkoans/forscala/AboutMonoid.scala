package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers

/**
  *
  */
trait Monoid[A] {
  def mappend(a1: A, a2: A): A

  def mzero: A
}

object Monoid {
  implicit val IntMonoid: Monoid[Int] = new Monoid[Int] {
    def mappend(a: Int, b: Int): Int = a + b

    def mzero: Int = 0
  }
  implicit val StringMonoid: Monoid[String] = new Monoid[String] {
    def mappend(a: String, b: String): String = a + b

    def mzero: String = ""
  }
}


class AboutMonoid extends KoanSuite with Matchers {


  /**
    * @see
    */
  koan("""FoldLeft operation on generalized List using Monoid operations""") {
    object FoldLeftList {
      def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B) = xs.foldLeft(b)(f)
    }


    def sum[A: Monoid](xs: List[A]): A = {
      val m = implicitly[Monoid[A]]
      FoldLeftList.foldLeft(xs, m.mzero, m.mappend)
    }

    sum(List(1, 2, 3, 4)) should be(10)
    sum(List("a", "b", "c")) should be("abc")
  }

  /**
    * @see
    */
  koan("""Extract a FoldLeft typeclass using Monoid operations""") {
    trait FoldLeft[F[_]] {
      def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
    }

    object FoldLeft {
      implicit val FoldLeftList: FoldLeft[List] = new FoldLeft[List] {
        def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B) = xs.foldLeft(b)(f)
      }
    }


    def sum[M[_] : FoldLeft, A: Monoid](xs: M[A]): A = {
      val m = implicitly[Monoid[A]]
      val fl = implicitly[FoldLeft[M]]
      fl.foldLeft(xs, m.mzero, m.mappend)
    }

    sum(List(1, 2, 3, 4)) should be(10)
    sum(List("a", "b", "c")) should be("abc")
  }

  koan("""Write function which sums 2 ints using Monoids""") {
    def plus[A: Monoid](a: A, b: A): A = implicitly[Monoid[A]].mappend(a, b)

    plus(3, 4) should be(7)
  }


  koan("""Injecting new methods using generic type operations using Monoids""") {
    //Scalaz code style
    trait MonoidOp[A] {
      val F: Monoid[A]
      val value: A
      def |+|(a2: A) = F.mappend(value, a2)
    }

    implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
      val F = implicitly[Monoid[A]]
      val value = a
    }

    3 |+| 4 should be (7)  // Method |+| is injected to both types ( Int and String) in one definition
    "a" |+| "b" should be ("ab")
  }
}
