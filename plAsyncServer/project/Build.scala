import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "plAsyncServer"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "commons-collections" % "commons-collections" % "3.2.1",
    "org.mockito" % "mockito-core" % "1.9.5",
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
