package org.functionalkoans.forscala

import scalaz._
import Scalaz._

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers
import scala.reflect.runtime.universe._
import scala.reflect._

import scala.language.implicitConversions

/**
  *
  */

class AboutYesNoType extends KoanSuite with Matchers {

  trait CanTruthy[A] { self =>
    /** @return true, if `a` is truthy. */
    def truthys(a: A): Boolean
  }
  object CanTruthy {
    def apply[A](implicit ev: CanTruthy[A]): CanTruthy[A] = ev
    def truthys[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
      def truthys(a: A): Boolean = f(a)
    }
  }
  trait CanTruthyOps[A] {
    def self: A
    implicit def F: CanTruthy[A]
    final def truthy: Boolean = F.truthys(self)
  }

  object ToCanIsTruthyOps {
    implicit def toCanIsTruthyOps[A](v: A)(implicit ev: CanTruthy[A]) =
      new CanTruthyOps[A] {
        def self = v
        implicit def F: CanTruthy[A] = ev
      }
  }

  /**
    * @see
    */
  koan("""Ad-hoc polymorphism on an Int type """) {
    implicit val intCanTruthy: CanTruthy[Int] = CanTruthy.truthys({
      case 0 => false
      case _ => true
    })

    import ToCanIsTruthyOps._

    var myNum: Int = 10
    myNum.truthy should be (true)
  }
  /**
    * @see
    */
  koan("""Ad-hoc polymorphism on an List type """) {
    implicit def listCanTruthy[A]: CanTruthy[List[A]] = CanTruthy.truthys({
      case Nil => false
      case _ => true
    })

    import ToCanIsTruthyOps._

    List("foo").truthy should be (true)
  }

}
