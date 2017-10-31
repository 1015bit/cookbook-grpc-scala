// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `advanced-grpc-scala-client` =
  project
      .in(file("client"))
      .enablePlugins(AutomateHeaderPlugin, GitVersioning)
      .settings(settings)
      .settings(
        libraryDependencies ++= Seq(
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
      )

lazy val `advanced-grpc-scala-protocol` =
  project
      .in(file("protocol"))
      .enablePlugins(AutomateHeaderPlugin, GitVersioning)
      .settings(settings)
      .settings(scalaPbSettings)
      .settings(
        libraryDependencies ++= Seq(
          library.grpcNetty,
          library.scalaPbRuntime,
          library.scalaPbRuntimeGrpc,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
      )

lazy val `advanced-grpc-scala-service` =
  project
      .in(file("service"))
      .enablePlugins(AutomateHeaderPlugin, GitVersioning)
      .settings(settings)
      .settings(
        libraryDependencies ++= Seq(
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
      )

lazy val `advanced-grpc-scala` =
  project
    .in(file("."))
    .aggregate(
      `advanced-grpc-scala-client`, 
      `advanced-grpc-scala-protocol`, 
      `advanced-grpc-scala-service`
      )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

import com.trueaccord.scalapb.compiler.{ Version => VersionPb }
lazy val library =
  new {
    object Version {
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.4"
    }
    val grpcNetty          = "io.grpc"                  % "grpc-netty"            % VersionPb.grpcJavaVersion
    val scalaCheck         = "org.scalacheck"          %% "scalacheck"            % Version.scalaCheck
    val scalaPbRuntime     = "com.trueaccord.scalapb"  %% "scalapb-runtime"       % VersionPb.scalapbVersion % "protobuf"
    val scalaPbRuntimeGrpc = "com.trueaccord.scalapb"  %% "scalapb-runtime-grpc"  % VersionPb.scalapbVersion
    val scalaTest          = "org.scalatest"           %% "scalatest"             % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.4",
    organization := "io.ontherocks",
    organizationName := "Petra Bierleutgeb",
    startYear := Some(2017),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-Xfatal-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalaPbSettings = Seq(
  PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.3.0"
  )
