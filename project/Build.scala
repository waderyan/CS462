import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "foursquare"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    cache,
    "org.scribe" % "scribe" % "1.3.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
