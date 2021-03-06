package org.functionalkoans.forscala

import scalaz._
import Scalaz._

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers
import scala.reflect.runtime.universe._
import scala.reflect._

import scala.language.implicitConversions

/**
  * @see http://eed3si9n.com/learning-scalaz/a+Yes-No+typeclass.html
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

    //Very important to have this import to point to the right context
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

  koan("""Ad-hoc and generic polymorphism """) {
    import ToCanIsTruthyOps._
    implicit val nilCanTruthy: CanTruthy[scala.collection.immutable.Nil.type] = CanTruthy.truthys(_ => false)
    implicit val booleanCanTruthy: CanTruthy[Boolean] = CanTruthy.truthys(identity)

    def truthyIf[A: CanTruthy, B, C](cond: A)(ifyes: => B)(ifno: => C) =
      if (cond.truthy) ifyes
      else ifno

    truthyIf (true) {"YEAH!"} {"NO!"} should be ("YEAH!")
    truthyIf (Nil) {"YEAH!"} {"NO!"} should be ("NO!")

    //Important to have this in the context, so compiler will find the implicit value for AboutYesNoType.this.CanTruthy[List[Int]]
    implicit def listCanTruthy[A]: CanTruthy[List[A]] = CanTruthy.truthys({
      case Nil => false
      case _ => true
    })
    truthyIf (2 :: 3 :: 4 :: Nil) {"YEAH!"} {"NO!"} should be ("YEAH!")
  }

}
