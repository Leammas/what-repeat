name := "what_repeat"

version := "0.1"

scalaVersion := "2.13.1"

lazy val catsVersion = "2.1.0"
lazy val catsEffectVersion = "2.1.2"
lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
lazy val fs2 = "co.fs2" %% "fs2-core" % "2.2.1"

libraryDependencies ++= Seq(
  catsEffect,
  fs2
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ypartial-unification",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

scalacOptions ++= commonScalacOptions