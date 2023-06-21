ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "scalagraphics-demo",
    organization := "com.github.jarlah.scalagraphics",
    idePackagePrefix := Some("com.github.jarlah.scalagraphics"),
    resolvers += "GitHub Package Registry" at "https://maven.pkg.github.com/jarlah/scalagraphics",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.16",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    libraryDependencies += "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % "test",
    libraryDependencies += "com.github.jarlah.scalagraphics" % "scalagraphics_3" % "0.3.1-SNAPSHOT"
  )
