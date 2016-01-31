package org.functionalkoans.forscala

import org.functionalkoans.forscala.support.KoanSuite
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

class Candy

class AboutTypeTags extends KoanSuite {

  koan("""TypeTags can be used to determine a type used
         |   before it erased by the VM by using an implicit TypeTag argument.""") {

    def inspect[T](l: List[T])(implicit tt: TypeTag[T]) = tt.tpe.typeSymbol.name.decoded
    val list = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
    inspect(list) should be("Int") /** @keypoint */
  }

  koan("""TypeTags can also be access using the context bound syntaxt""") {

    def inspect[T : TypeTag](l: List[T]) = typeOf[T].typeSymbol.name.decoded
    val list = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
    inspect(list) should be("Int") /** @keypoint */
  }

  koan("""TypeTags can be attached to classes. TypeTags have other meta-information about
         |  the type erased""") {

    class Barrel[T](implicit tt:TypeTag[T]) {
      def +(t: T) = "1 %s has been added".format(tt.tpe.typeSymbol.name.decoded) //Simple-name of the class erased
    }
    val candyBarrel = new Barrel[Candy]

    (candyBarrel + new Candy) should be("1 Candy has been added") /** @keypoint */
  }

  koan("""ClassTags can help compare types at runtime""") {

    /**
      * @see http://stackoverflow.com/a/19388888/4032515
      */
    import scala.reflect.ClassTag

    def sameType(a: Any, b: Any) = {
      val B = ClassTag(b.getClass)
      ClassTag(a.getClass) match {
        case B => true
        case _ => false
      }
    }

    val int1: Any = 5
    val int2 = 6
    val string = "abc"

    sameType(int1, int2) should be(true) /** @keypoint */
    sameType(int1, string) should be(false) /** @keypoint */
  }

  koan("""TypeTags can be retrieved with `typeTag`""") {

    /**
      * @see http://stackoverflow.com/a/11495793/4032515
      */

    def tt[A : TypeTag](a: A) = typeTag[A]

    val ts = tt("abc")

    ts.tpe.typeSymbol.name.decoded should be ("String")

  }

  koan("""TypeTags can be created manually""") {

    /**
      * @see http://stackoverflow.com/questions/11494788/how-to-create-a-typetag-manually
      */

    //@todo

  }

  koan("""TypeTags can be used to instantiate types by reflection""") {

    /**
      * @see http://stackoverflow.com/questions/22970209/get-typetaga-from-classa/22972751#22972751
      */
    import scala.reflect._

    class Fruit
    class Orange extends Fruit
    class Apple extends Fruit
    class Banana extends Fruit

    def createNew_1[A <: Fruit](implicit tt: TypeTag[A]) : A = {

      val result = tt.tpe.typeSymbol.getClass match {
        case o1 if o1.isAssignableFrom(classOf[Orange]) => new Orange()
        case o2 if o2.isAssignableFrom(classOf[Apple]) => new Apple()
        case _ => throw new IllegalArgumentException
      }
      result.asInstanceOf[A]
    }

    createNew_1[Orange].isInstanceOf[Orange] should be(true)
    createNew_1[Apple].isInstanceOf[Apple] should be(true)
    intercept[IllegalArgumentException] {
      createNew_1[Banana].isInstanceOf[Banana] should be(true)
    }

//    /**
//      * Method 2 of way to do it
//      */
//    implicit class ClassTagOps[T](val classTag: ClassTag[T]) extends AnyVal {
//      def <<:(other: ClassTag[_]) = classTag.runtimeClass.isAssignableFrom(other.runtimeClass)
//    }
//
//    def createNew_2[A <: Fruit]: A = {
//      val result = classTag[A] match {
//        case a if a <<: classTag[Orange] => new Orange()
//        case a if a <<: classTag[Apple] => new Apple()
//        case _ => throw new IllegalArgumentException
//      }
//      result.asInstanceOf[A]
//    }
//
//    createNew_2[Orange].isInstanceOf[Orange] should be(true)
//    createNew_2[Apple].isInstanceOf[Apple] should be(true)
//    intercept[IllegalArgumentException] {
//      createNew_2[Banana].isInstanceOf[Banana] should be(true)
//    }


  }


}