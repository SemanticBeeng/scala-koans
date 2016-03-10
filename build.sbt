addCommandAlias("namaste", "~test-only org.functionalkoans.forscala.Koans")

name := "Scala Koans"

version := "1.0"

scalaVersion := "2.11.7"

traceLevel := -1

logLevel := Level.Info

// disable printing timing information, but still print [success]
showTiming := false

// disable printing a message indicating the success or failure of running a task
showSuccess := false

// append -deprecation to the options passed to the Scala compiler
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// disable updating dynamic revisions (including -SNAPSHOT versions)
offline := true

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "2.2.6" % "test" withSources() withJavadoc()
)


//Scalaz
val scalazVersion = "7.2.0"

libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion

//libraryDependencies ++= Seq(
//	"org.scalaz" %% "scalaz-core" % scalazVersion,
//	"org.scalaz" %% "scalaz-effect" % scalazVersion,
//	"org.scalaz" %% "scalaz-typelevel" % scalazVersion,
//	"org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test"
//)

initialCommands in console := "import scalaz._, Scalaz._"