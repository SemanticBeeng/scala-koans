package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite

import scala.reflect._
import scala.xml.Node


/**
  * @see http://www.casualmiracles.com/2012/05/03/a-small-example-of-the-typeclass-pattern-in-scala/
  */


/** Here is the first piece of the Scala typeclass puzzle, its just a trait.
  * The trait defines a concept which in this case is a transformer
  * that transforms a type T into a R.
  */
trait Transformer[T, R] {
  def transform(t: T): R
}

/** This is a companion object for the typeclass giving default implementations for the typeclass.
  * These implementations are found after local implicits, so you can still override the default
  * behaviour. For more about the search order see [3].
  * */
object Transformer {

  implicit object IntToStringTransformer extends Transformer[Int, String] {
    def transform(t: Int) = t.toString
  }

  /** It transforms a List[T] into a String, but to do that it
    * needs a transformer for T to String. So it requires such a transformer implicitly.
    * */
  implicit def ListToStringTransformer[T](implicit tToString: Transformer[T, String]) = new Transformer[List[T], String] {
    //Method with argument T, returning a String and calling tToString
    def transformTypeToString: (T) => String = {
      tToString.transform(_)
    }

    def transform(t: List[T]) = t.map(transformTypeToString).mkString(",")
  }
}

// This is something that makes use of the typeclass
trait Transform {
  // The implicit Transformer, transformer, supplies an appropriate transformer for the method
  def transform[T, R](t: T)(implicit transformer: Transformer[T, R]): R = transformer.transform(t)
}


class AboutTypeTransform extends KoanSuite with Transform {

  koan( """Use default implementations of Transformer's companion object, for transforming Int to String""") {
    transform(2) should be("2")
    transform(List(1, 2, 3)) should be("1,2,3")
  }


  koan(
    """Special Transformer defined in a context will get invoked, for transforming an Int to XML node""") {
    implicit object MyIntToXmlTransformer extends Transformer[Int, xml.Node] {
      def transform(t: Int) = <aNumber>{t.toString}</aNumber>
    }

    val node: Node = transform(2)

    node.getClass.getCanonicalName should be("scala.xml.Elem")
    node.toString() should be("<aNumber>2</aNumber>")
  }

  koan("""Special Transformer defined in a context will get invoked, for transforming a List[Int] to XML nodes""") {
    implicit object MyIntToXmlTransformer extends Transformer[Int, xml.Node] {
      def transform(t: Int) = <aNumber>{t.toString}</aNumber>
    }

    // As with the default List transformer, this one needs a transformer of T to Node so that it can perform the transform for lists.
    implicit def MyListToXmlTransformer[T](implicit transformer: Transformer[T, xml.Node]) = new Transformer[List[T], xml.NodeSeq] {

      import xml._

      def transform(t: List[T]): NodeSeq = t.foldLeft(NodeSeq.Empty)((accumulator, next) ⇒ accumulator ++ transformer.transform(next))
    }


    val nodeSeq = transform(List(1, 2, 3))

    nodeSeq.getClass.getName should be("scala.xml.NodeSeq$$anon$1")
    nodeSeq.toString() should be("<aNumber>1</aNumber><aNumber>2</aNumber><aNumber>3</aNumber>")
  }

  koan("""Transformer from Boolean to String used in transforming a List of Booleans""") {
    implicit object BooleanToStringTransformer extends Transformer[Boolean, String] {
      def transform(b: Boolean) = b.toString
    }

    // This is the clever bit.
    // Now we can now transform List[Boolean] to String. The List transformer defined in the default Transformer trait
    // will be used but with the new BooleanToStringTransformer. So the default transformers and the new ones
    // work together.
    val boolTransformed: String = transform(List(true, false))
    boolTransformed should be ("true,false")

  }

  koan( """Transforming a String to String, it requires a Transformer""") {
    /**
      * @doesnotcompile
      *                without specifying the transformer from String to String, as there are no implicits available
      *                . This Transformer should be parameterized in [String, R], where R = String (particular case)
      * <code>
      * transform("3")
      * </code>
      */

    // Declaring a transformer from String to String
    implicit object StringToStringTransformer extends Transformer[String, String] {
      def transform(s: String) = s
    }

    transform("3")
  }

}