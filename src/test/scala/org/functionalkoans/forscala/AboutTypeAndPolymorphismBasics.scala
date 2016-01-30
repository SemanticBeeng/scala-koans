package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers

/**
  * Source: http://twitter.github.io/scala_school/type-basics.html
  */
class AboutTypeAndPolymorphismBasics extends KoanSuite with Matchers {


  koan("""Without parametric polymorphism, a generic list data structure would always look like this""") {

    import scala.reflect.runtime.universe._
    def inspect[T](l: List[T])(implicit tt: TypeTag[T]) = tt.tpe

    val list = 2 :: 1 :: "bar" :: "foo" :: Nil

    inspect(list).fullName should be("Any")
  }

  koan("""Some type concepts you’d like to express in Scala that are “too generic” for the compiler to understand""") {

    def toList[A](a: A) = List(a)

    def foo[A](f: A => List[A], a: A) = f(a)

    /**
      * @doesnotcompile This does not compile, because all type variables have to be fixed at the invocation site
      * @see http://twitter.github.io/scala_school/type-basics.html
      * <code>
          foo(toList, 10)
      * </code>
      **/

    foo[Int](toList, 10) should be(List(10)) /** @keypoint */
  }


  /**
    * @see https://twitter.github.io/scala_school/type-basics.html#inference
    */
  koan("""In scala all type inference is local. Scala considers one expression at a time""") {

    def id[T](x: T) = x

    val x = id(322)

    x should be(322)

    // @see http://stackoverflow.com/a/19388888/4032515
    import scala.reflect.ClassTag
    val tag: ClassManifest[Int] = ClassTag(x.getClass)
  }


}