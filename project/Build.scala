import sbt._
import sbt.Keys._

// Scalariform
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform._
import ScalariformKeys._

import com.typesafe.sbt.pgp.PgpSettings.useGpg

object Build extends Build {

   lazy val macrame: Project = Project(
      "macrame",
      file("macrame"),
      settings = commonSettings ++ Seq(
         version := "1.2.7",
         libraryDependencies ++= Seq(
            "org.scala-lang" % "scala-compiler" % scalaVersion.value,
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
            "org.scalatest" %% "scalatest" % "3.0.0" % "test"),
         libraryDependencies ++=
            (CrossVersion.partialVersion(scalaVersion.value) match {
               case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
               case Some((2, 10)) => Seq(
                  "org.scalamacros" %% "quasiquotes" % "2.0.1")
            })))

   lazy val macramePlay: Project = Project(
      "macrame-play",
      file("macrame-play"),
      settings = commonSettings ++ Seq(
         version := "1.1.2-play-2.6.x",
         libraryDependencies ++= Seq(
            "com.typesafe.play" %% "play" % "[2.6,2.7[" % Provided,
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % "test" cross CrossVersion.full),
            "org.scalatest" %% "scalatest" % "3.0.0" % "test"))
      ).dependsOn(macrame)

   lazy val macrameScalaz: Project = Project(
      "macrame-scalaz",
      file("macrame-scalaz"),
      settings = commonSettings ++ Seq(
         version := "1.0.2-scalaz-7.2.x",
         libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "[7.2,7.3[" % Provided,
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % "test" cross CrossVersion.full),
            "org.scalatest" %% "scalatest" % "3.0.0" % "test",
            "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"))
   ).dependsOn(macrame)

   lazy val pomStuff = {
     <url>https://github.com/ChrisNeveu/macrame</url>
     <licenses>
       <license>
         <name>BSD 3-Clause</name>
         <url>https://raw.githubusercontent.com/ChrisNeveu/macrame/master/LICENSE</url>
       </license>
     </licenses>
     <scm>
       <connection>scm:git:github.com/gawkermedia/macrame.git</connection>
       <developerConnection>scm:git:git@github.com:gawkermedia/macrame.git</developerConnection>
       <url>git@github.com:gawkermedia/macrame.git</url>
     </scm>
     <developers>
       <developer>
         <name>Chris Neveu</name>
         <url>kinja.com</url>
       </developer>
     </developers>
   }

   lazy val commonSettings = Defaults.coreDefaultSettings ++ scalariformSettings ++ Seq(
      organization := "com.kinja",
      scalaVersion := "2.11.8",
      crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0"),
      scalacOptions ++= Seq(
         "-unchecked",
         "-deprecation",
         "-feature",
         "-language:higherKinds",
         "-language:postfixOps"
      ),
      useGpg := true,
      pomExtra := pomStuff,
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
         .setPreference(IndentSpaces, 3)
         .setPreference(SpaceBeforeColon, true)
         .setPreference(PreserveDanglingCloseParenthesis, true)
         .setPreference(RewriteArrowSymbols, true)
         .setPreference(DoubleIndentClassDeclaration, true)
         .setPreference(AlignParameters, true)
         .setPreference(AlignSingleLineCaseStatements, true)
   )
}
