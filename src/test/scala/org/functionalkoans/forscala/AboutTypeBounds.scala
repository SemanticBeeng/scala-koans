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
      * <code>
           val y: Int = "123"
      * </code>
      **/
    implicit def strToInt(x: String) = x.toInt
    val y: Int = "123"
    y should be (123)

  }

}
