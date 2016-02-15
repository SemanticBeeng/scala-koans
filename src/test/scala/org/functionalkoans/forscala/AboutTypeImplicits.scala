package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite

/**
  *
  */

trait CanFoo[A] {
  def foos(x: A): String
}

//Companion object
object CanFoo {
  def apply[A: CanFoo]: CanFoo[A] = {
    println("In CanFoo companion object")
    implicitly
  }
}

case class Wrapper(wrapped: String)


class AboutTypeImplicits extends KoanSuite {

  //Typeclasses let you split the definition of the class and implementation of its interface
  implicit object WrapperCanFoo extends CanFoo[Wrapper] {
    def foos(x: Wrapper) = {
      println("In WrapperCanFoo")
      x.wrapped
    }
  }

  koan("Implicit Type parameters are getting passed to companion object, if they are found in scope") {

    //WrapperCanFoo is the only in the declaration scope, so it gets called implicitly and irs implemented method
    // foos is invoked
    def foo[A: CanFoo](thing: A) = CanFoo[A].foos(thing)
    foo(Wrapper("hi")) should be("hi")
  }

  koan("Implicit conversion used to make methods like they are available in the object") {

    // CanFoo and companion object are in scope
    implicit class CanFooOps[A: CanFoo](thing: A) {
      println("In CanFooOps")

      //Confusing notation: CanFoo parameterized in CanFoo
      //@todo review again: how come this method gets assigned to CanFoo?
      def foo = CanFoo[A].foos(thing)
    }

    //Type A has now method foo, through CanFooOps.foo method, such as: CanFooOps(thing).foo
    def foo[A: CanFoo](thing: A) = thing.foo

    foo(Wrapper("hello")) should be("hello")
  }

}
