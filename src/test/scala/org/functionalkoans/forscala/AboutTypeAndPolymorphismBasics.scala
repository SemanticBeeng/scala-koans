package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import org.scalatest.Matchers
import scala.reflect.runtime.universe._
import scala.reflect._

/**
  * @see http://twitter.github.io/scala_school/type-basics.html
  * @see http://docs.scala-lang.org/overviews/reflection/typetags-manifests.html
  */
class AboutTypeAndPolymorphismBasics extends KoanSuite with Matchers {


  koan( """Without parametric polymorphism, a generic list data structure would always look like this""") {

    def manifestOf[T](l: List[T])(implicit tm: Manifest[T]) = tm

    manifestOf(2 :: 1 :: "bar" :: "foo" :: Nil) should be(Manifest.Any)

    manifestOf(2 :: 1 :: 3 :: 4 :: Nil) should be(Manifest.Int)
  }

  koan(
    """Scala has rank-1 polymorphism |
      | Some type concepts you’d like to express in Scala that are “too generic” for the compiler to understand"""
      .stripMargin) {

    def toList[A](a: A) = List(a)

    def foo[A](f: A => List[A], a: A) = f(a)

    /**
      * @doesnotcompile This does not compile, because all type variables have to be fixed at the invocation site
      * @see http://twitter.github.io/scala_school/type-basics.html
      *      <code>
          foo(toList, 10)
      *      </code>
      **/

    foo[Int](toList, 10) should be(List(10))

    /** @keypoint */
  }


  /**
    * @see https://twitter.github.io/scala_school/type-basics.html#inference
    */
  koan(
    """In scala all type inference is local
      | Scala considers one expression at a time""".stripMargin) {

    // @see http://stackoverflow.com/a/19388888/4032515
    import scala.reflect.ClassTag

    def id[T](x: T) = x

    ClassTag(id(322).getClass) should be(ClassTag.Int)

    val v2: Float = 12.3f
    ClassTag(id(v2).getClass) should be(ClassTag.Float)

    //@todo
    // val v3 = Array(1,2,3,4)
    // ClassTag(id(v3).getClass) should be(ClassTag[scala.Array])

  }

  /**
    * @see http://twitter.github.io/scala_school/type-basics.html#variance
    */
  koan(
    """Contravariance is used in defining functions
      | Arguments are contravariant and return values are covariant""".stripMargin) {

    class Animal {
      val sound = "rustle"
    }
    class Bird extends Animal {
      override val sound = "call"
    }
    class Chicken extends Bird {
      override val sound = "cluck"
    }
    class Duck extends Bird {
      override val sound = "quack"
    }

    /**
      * Function parameters are contravariant.
      * A function that takes a Bird can be assigned a function that takes an Animal
      */
    def getSoundOfAnimal(a: Animal): String = a.sound

    var getTweetOfBird: (Bird => String) = getSoundOfAnimal

    getTweetOfBird(new Bird) should be("call")

    /**
      * Function parameters are contravariant.
      * A function that takes a Bird canNOT be assigned a function that takes a Duck:
      */
    val getSoundOfDuck: (Duck) => String = (a: Duck) => a.sound

    /**
      * @doesnotcompile
      * <code>
          getTweetOfBird = getSoundOfDuck
          getTweetOfBird(new Bird) should be("cluck")
      * </code>
      */

    /**
      * Function return type is covariant.
      * A function that returns a Bird can be assigned a function that takes a Duck
      */
    val hatch: () => Bird = () => new Duck

    getTweetOfBird(hatch()) should be("quack")
  }

  /**
    * @see http://twitter.github.io/scala_school/type-basics.html#bounds
    */
  koan(
    """Bounds can restrict polymorphic variables
      | These bounds express subtype relationships.""".stripMargin) {

    class Animal {
      val sound = "rustle"
    }
    class Bird extends Animal {
      override val sound = "call"
    }
    class Chicken extends Bird {
      override val sound = "cluck"
    }
    class Duck extends Bird {
      override val sound = "quack"
    }

    /**
      * @doesnotcompile
      * <code>
           def cacophony[T](things: Seq[T]) = things map (_.sound)
      * </code>
      **/
    def biophony[T <: Animal](things: Seq[T]) = things map (_.sound)

    biophony(Seq(new Chicken, new Bird)) should be(Seq("cluck", "call"))
  }

  /**
    * @see http://twitter.github.io/scala_school/type-basics.html#bounds
    */
  koan(
    """Bounds can restrict polymorphic variables
      | The List type uses contravariance and clever covariance.""".stripMargin) {

    class Animal {
      val sound = "rustle"
    }
    class Bird extends Animal {
      override val sound = "call"
    }
    class Chicken extends Bird {
      override val sound = "cluck"
    }
    class Duck extends Bird {
      override val sound = "quack"
    }

    val flock = List(new Bird, new Bird)

    def typeName[A : TypeTag](a: A) = typeTag[A].tpe.toString

    val a1: List[Bird] = new Chicken :: flock
    typeName(a1) should be("List[Bird]]")

    typeName(new Animal :: flock) should be("List[Animal]]") /** @keypoint */
  }

  /**
    * @see http://twitter.github.io/scala_school/type-basics.html#quantification
    */
  koan(
    """Bounds can apply to wildcard type variables
      | ....""".stripMargin) {

    def typeName[A : TypeTag](a: A) = typeTag[A].tpe.toString

    def countA[A](l: List[A]) = l.size

    def countWild(l: List[_]) = l.size
    typeName(countWild _) should be("scala.List[_] => Int")

    // feature should be explicitly enabled
    //def countHeavySyntax(l: List[T forSome { type T }]) = l.size

    def dropWild(l: List[_]) = l.tail
    typeName(dropWild _) should be("scala.List[_] => List[Any]")

    def hashcodes(l: Seq[_ <: AnyRef]) = l map (_.hashCode)
    typeName(hashcodes _) should be("scala.Seq[_ <: scala.AnyRef] => Seq[Int]")
  }


}
