import sbt.Keys.credentials
import sbt.{Credentials, Developer, Path, ScmInfo, url}

val Http4sVersion = "0.20.11"
val Specs2Version = "4.7.1"
val LogbackVersion = "1.2.3"
val GoogleOauthClientVersion = "1.22.0"
val CirceVersion = "0.10.1"
val scalaLoggingVersion = "3.9.2"

organization := "com.akolov"
name := "doorman"
scalaVersion := "2.13.0"

description := "Oauth2 authentication and user session middleware for http4s"
licenses := Seq("MIT License" -> url("https://github.com/kolov/doorman/blob/master/LICENSE"))

homepage := Some(url("https://github.com/kolov/doorman"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/kolov/doorman"),
    "scm:git@github.com:kolov/doorman.git"
  )
)

developers := List(
  Developer(
    id    = "kolov",
    name  = "Assen Kolov",
    email = "assen.kolov@gmail.com",
    url   = url("https://github.com/kolov")
  )
)

lazy val commonSettings = Seq(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.11.0")
)

lazy val testDependencies = Seq(
  "org.specs2" %% "specs2-core" % Specs2Version % "test",
  "org.specs2" %% "specs2-mock" % Specs2Version % "test",
  "com.codecommit" %% "cats-effect-testing-specs2" % "0.3.0" % "test"

)

lazy val core = (project in file("core")).settings(
  name := "doorman-core",
  commonSettings,
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion 
  ) ++ testDependencies,
  publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
  credentials += Credentials(Path.userHome / ".sonatype" / ".credentials")

)

lazy val demo = (project in file("demo"))
  .dependsOn(core)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.auth0" % "java-jwt" % "3.2.0",
      "com.github.pureconfig" %% "pureconfig" % "0.12.2",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
    ) ++ testDependencies
  )

lazy val root = (project in file("."))
  .aggregate(demo, core)

lazy val docs = project       // new documentation project
  .in(file("project-docs")) // important: it must not be docs/
  .dependsOn(core)
  .enablePlugins(MdocPlugin)
  .settings (
    mdocOut := new java.io.File ("README.md")
  )





