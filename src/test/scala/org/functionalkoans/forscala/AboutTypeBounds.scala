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
    def foo(x: {def get: Int}) = 123 + x.get
    foo(new {
      def get = 10
    }) should be(133)
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
    (new Foo {
      type A = Int;
      val x = 123
    }).getX should be(123)

    (new Foo {
      type A = String;
      val x = "hey"
    }).getX should be("hey")
  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#abstractmem
    */
  koan("""Abstract types variables can be referred with hash operator #""") {
    trait Foo[M[_]] {
      type t[A] = M[A]
    }

    val x: Foo[List]#t[Int] = List(1)

    x should be(List(1))
  }

  /**
    * @see http://twitter.github.io/scala_school/advanced-types.html#manifest
    */
  koan("""Use Manifests to selectively recover type information at compile time""") {
    class MakeFoo[A](implicit manifest: Manifest[A]) {
      def make: A = manifest.erasure.newInstance.asInstanceOf[A]
    }

    new MakeFoo[String].make should be("")

    //Without the manifest
    class MakeNoFoo[A] {
      def make: A = asInstanceOf[A]
    }

    /**
      * @doesnotcompile Class cast exception, can not cast MakeNoFoo to String
      *
      *                 <code>
      *                 (new MakeNoFoo[String]).make
      *                 </code>
      **/
  }


  /**
    * @see https://github.com/deanwampler/prog-scala-2nd-ed-code-examples/tree/master/src/main/scala/progscala2/typesystem
    */
  koan("""View bounds A <% B are used for automated conversion from on type to other with LinkedLists""") {

    //Import the hierarchy of nodes
    import org.functionalkoans.forscala.bounds._

    // converts type A to Node[A], by creating a “leaf” node using a bounds.:: node with a reference to NilNode as the
    // “next” element in the list.
    implicit def any2Node[A](x: A): Node[A] = org.functionalkoans.forscala.bounds.::[A](x, NilNode)

    //Defines a view bound on A, starting from the head of the nodes chain
    case class LinkedList[A <% Node[A]](val head: Node[A]) {

      //The type parameter means ``B is lower bounded by (i.e., is a supertype of) A, and B also
      //has a view bound of B <% Node[B]. The expression applies to B, as being the ID (according to the grammar for
      // type parameters)
      def ::[B >: A <% Node[B]](x: Node[B]) =
        LinkedList(org.functionalkoans.forscala.bounds.::(x.payload, head))

      override def toString = head.toString
    }
    //Automatic conversion of dealing with Int and Strings taken care of
    val list1 = LinkedList(1)
    val list2 = 2 :: list1
    val list3 = 3 :: list2
    val list4 = "FOUR!" :: list3
    println(list1)
    println(list2)
    println(list3)
    println(list4)
  }

  koan("""Path dependent types in nested types""") {

    trait Service {

      trait Logger {
        def log(message: String): Unit
      }

      val logger: Logger
      def run = {
        logger.log("Starting " + getClass.getSimpleName + ":")
        doRun
      }

      protected def doRun: Boolean
    }
    object MyService1 extends Service {

      class MyService1Logger extends Logger {
        def log(message: String) = println("1: " + message)
      }
      override val logger = new MyService1Logger
      def doRun = true // do some real work...
    }

    object MyService2 extends Service {


      /**
        * @doesnotcompile The nested Logger type in each MyServiceX object is unique for each of the service
        * types. The actual logger type is path-dependent.
        *
        * <code>
        * override val logger = MyService1.logger
        * </code>
        **/
      class MyService2Logger extends Logger {
        def log(message: String) = println("2: " + message)
      }
      override val logger = new MyService2Logger
      def doRun = true // do some real work...
    }

  }

}
