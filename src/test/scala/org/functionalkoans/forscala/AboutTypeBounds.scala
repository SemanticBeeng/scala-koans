package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers

/**
  *
  */
class AboutTypeBounds extends KoanSuite with Matchers {

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#viewbounds
    */
  koan("""A view bound specifies a type that can be “viewed as” another""") {

    /**
      * @doesnotcompile Type mismatch as no converter has been defined in this context yet
      *                 <code>
           val y: Int = "123"
      *                 </code>
      **/
    implicit def strToInt(x: String) = x.toInt
    val y: Int = "123"
    y should be(123)

  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#otherbounds
    */
  koan("""Other type bounds are enforced via implicit parameters""") {

    class Container[A](value: A) {
      def addIt(implicit evidence: A =:= Int) = 123 + value
    }

    (new Container(123)).addIt should be(246)

    /**
      * @doesnotcompile Can't prove String as being equal =:= with Int
      *                 <code>
          (new Container("123")).addIt
      *                 </code>
      **/

    /**
      * @doesnotcompile It should compile, when relaxing the viewability constraint to sub-type <%<
      * @todo Fix this error
      *       <code>
          class Container[A](value: A) { def addIt(implicit evidence: A <%< Int) = 123 + value }
      *       </code>
      **/

  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#structural
    */
  koan("""Structural types are expressed through interface structure and not through concrete types""") {
    def foo(x: { def get: Int }) = 123 + x.get
    foo(new {def get = 10}) should be (133)
  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#abstractmem
    */
  koan("""In a trait member types can be left abstract""") {
    trait Foo {
      type A
      val x: A
      def getX: A = x
    }
    (new Foo { type A = Int; val x = 123 }).getX should be (123)

    (new Foo { type A = String; val x = "hey" }).getX should be ("hey")
  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#abstractmem
    */
  koan("""Abstract types variables can be referred with hash operator #""") {
    trait Foo[M[_]] { type t[A] = M[A] }

    val x: Foo[List]#t[Int] = List(1)

    x should be (List(1))
  }
  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#manifest
    */
  koan("""Use Manifests to selectively recover type information at compile time""") {
    class MakeFoo[A](implicit manifest: Manifest[A]) {
      def make: A = manifest.erasure.newInstance.asInstanceOf[A]
    }

    new MakeFoo[String].make should be ("")

    //Without the manifest
    class MakeNoFoo[A]{ def make: A = asInstanceOf[A] }
    /**
      * @doesnotcompile Class cast exception, can not cast MakeNoFoo to String
      *
      *  <code>
      *  (new MakeNoFoo[String]).make
      * </code>
      **/
  }



}
