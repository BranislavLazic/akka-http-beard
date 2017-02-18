// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `akka-http-beard` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.akkaHttp,
        library.beard,
        library.akkaHttpTestkit % Test,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akkaHttp   = "10.0.3"
      val beard      = "0.2.0"
      val scalaCheck = "1.13.4"
      val scalaTest  = "3.0.1"

    }
    val beard           = "de.zalando"        %% "beard"             % Version.beard
    val akkaHttp        = "com.typesafe.akka" %% "akka-http"         % Version.akkaHttp
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
    val scalaCheck      = "org.scalacheck"    %% "scalacheck"        % Version.scalaCheck
    val scalaTest       = "org.scalatest"     %% "scalatest"         % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
  gitSettings ++
  headerSettings

lazy val commonSettings =
  Seq(
    // scalaVersion and crossScalaVersions from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.1",
    // crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "org.akkahttpbeard",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.HeaderPattern
import de.heikoseeberger.sbtheader.license._
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2017", "Branislav Lazic"))
  )

resolvers ++= Seq(
  "zalando-maven" at "https://dl.bintray.com/zalando/maven"
)