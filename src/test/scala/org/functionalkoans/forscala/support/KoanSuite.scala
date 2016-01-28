package org.functionalkoans.forscala.support

import org.scalatest.exceptions.TestPendingException
import org.scalatest._
import org.scalatest.events.{TestPending, TestFailed, TestIgnored, Event}
import org.scalatest.matchers.Matcher

trait KoanSuite extends FunSuite with Matchers {

  def koan(name : String)(fun: => Unit) { test(name.stripMargin('|'))(fun) }

  def meditate() = pending

  def  __ : Matcher[Any] = {
    throw new TestPendingException
  }

  protected class ___ extends Exception {
    override def toString = "___"
  }

  private class ReportToTheMaster(other: Reporter) extends Reporter {
    var failed = false
    def failure(event: Master.HasTestNameAndSuiteName) {
      failed = true
      /*info*/println("*****************************************")
      /*info*/println("*****************************************")
      /*info*/println("")
      /*info*/println("")
      /*info*/println("")
      /*info*/println(Master.studentFailed(event))
      /*info*/println("")
      /*info*/println("")
      /*info*/println("")
      /*info*/println("*****************************************")
      /*info*/println("*****************************************")
    }

    def apply(event: Event) {
      event match {
        case e: TestIgnored => failure(event.asInstanceOf[Master.HasTestNameAndSuiteName])
        case e: TestFailed => failure(event.asInstanceOf[Master.HasTestNameAndSuiteName])
        case e: TestPending => failure(event.asInstanceOf[Master.HasTestNameAndSuiteName])
        case _ => other(event)
      }

    }
  }

  override protected def runTest(testName: String, args: Args): Status = {

    if (!Master.studentNeedsToMeditate) {
      super.runTest(testName, args.copy(stopper = Master, reporter = new ReportToTheMaster(args.reporter)))
    } else {
      FailedStatus
    }
  }

}
