// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `advanced-grpc-scala-client` =
  project
      .in(file("client"))
      .enablePlugins(AutomateHeaderPlugin, GitVersioning)
      .dependsOn(`advanced-grpc-scala-protocol`)
      .settings(settings)
      .settings(
        libraryDependencies ++= Seq(
          library.cats,
          library.log4j2,
          library.log4j2Scala,
          library.monix,
          library.pureConfig,
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
      .dependsOn(`advanced-grpc-scala-protocol`)
      .settings(settings)
      .settings(
        libraryDependencies ++= Seq(
          library.cats,
          library.log4j2,
          library.log4j2Scala,
          library.monix,
          library.pureConfig,
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
      val cats        = "1.0.0-RC1"
      val log4j2      = "2.9.1"
      val log4j2Scala = "11.0"
      val monix       = "2.3.0"
      val pureconfig  = "0.8.0"
      val scalaCheck  = "1.13.5"
      val scalaTest   = "3.0.4"
    }
    val cats               = "org.typelevel"             %% "cats-core"             % Version.cats
    val grpcNetty          = "io.grpc"                    % "grpc-netty"            % VersionPb.grpcJavaVersion
    val log4j2             = "org.apache.logging.log4j"   % "log4j-core"            % Version.log4j2
    val log4j2Scala        = "org.apache.logging.log4j"   % "log4j-api-scala_2.12"  % Version.log4j2Scala
    val monix              = "io.monix"                  %% "monix"                 % Version.monix
    val pureConfig         = "com.github.pureconfig"     %% "pureconfig"            % Version.pureconfig
    val scalaCheck         = "org.scalacheck"            %% "scalacheck"            % Version.scalaCheck
    val scalaPbRuntime     = "com.trueaccord.scalapb"    %% "scalapb-runtime"       % VersionPb.scalapbVersion % "protobuf"
    val scalaPbRuntimeGrpc = "com.trueaccord.scalapb"    %% "scalapb-runtime-grpc"  % VersionPb.scalapbVersion
    val scalaTest          = "org.scalatest"             %% "scalatest"             % Version.scalaTest
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
      "-Ypartial-unification",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused-import",
      "-Ywarn-unused",
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
