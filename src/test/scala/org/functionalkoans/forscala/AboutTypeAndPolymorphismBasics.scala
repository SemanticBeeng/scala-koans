package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers

/**
  * Source: http://twitter.github.io/scala_school/type-basics.html
  */
class AboutTypeAndPolymorphismBasics extends KoanSuite with Matchers {


  koan("""Without parametric polymorphism, a generic list data structure would always look like this""") {

    import scala.reflect.runtime.universe._
    def inspect[T](l: List[T])(implicit tt: TypeTag[T]) = tt.tpe.typeConstructor

    val list = 2 :: 1 :: "bar" :: "foo" :: Nil

    inspect(list) should be(classOf[Any])
  }



}