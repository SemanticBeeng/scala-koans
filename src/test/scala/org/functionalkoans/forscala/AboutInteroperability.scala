package org.functionalkoans.forscala

import org.scalatest.{Ignore, Matchers}
import support.KoanSuite

@Ignore
class AboutInteroperability extends KoanSuite with Matchers {
  koan("""You can interop with a java class and it's use of collections by importing
          |   scala.collection.JavaConversions and letting scala implicitly convert
          |   from a Scala collection type
          |   into a Java collection type.  See AboutImplicits Koan Suite for more details
          |   and see src/test/java for the
          |   SomeJavaClass file. This koan
          |   converts a scala List of String to java List of raw type.""") {

    import scala.collection.JavaConversions._
    val d = new SomeJavaClass
    val e = List("one", "two", "three")
    d.findSizeOfRawType(e) should be(__)
  }

  class Boat(size: Int, manufacturer: String)

  koan("""This koan converts a scala List of Boat (our own class) to java List of unknown <?> type.""") {
    import scala.collection.JavaConversions._
    val d = new SomeJavaClass
    val e = List(new Boat(33, "Skyway"), new Boat(35, "New Boat"))
    d.findSizeOfUnknownType(e) should be(__)
  }
}
