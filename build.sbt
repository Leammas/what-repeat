name := "what_repeat"

version := "0.1"

scalaVersion := "2.13.1"

lazy val catsVersion = "2.1.0"
lazy val catsEffectVersion = "2.1.2"
lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

libraryDependencies ++= Seq(
  catsEffect
)