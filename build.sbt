import scalariform.formatter.preferences._

inThisBuild(Seq(
   organization := "com.kinja",
   organizationName := "Kinja",
   organizationHomepage := Some(url("https://kinja.com/")),

   scalaVersion := "2.12.8",
   crossScalaVersions := Seq("2.12.8", "2.11.8"),
   scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:higherKinds",
      "-language:postfixOps"
   ),
   
   scalariformPreferences := scalariformPreferences.value
      .setPreference(IndentSpaces, 3)
      .setPreference(SpaceBeforeColon, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
))

lazy val root = Project("root", file("."))
   .settings(
      publishArtifact := false,
      publish / skip := true
   )
   .aggregate(macrame, macramePlay, macrameScalaz)

lazy val macrame = Project("macrame", file("macrame"))
   .settings(
      version := "1.2.10-SNAPSHOT",
      publishTo := sonatypePublishTo.value,
      sonatypeProjectHosting := (Global / sonatypeProjectHosting).value,
      libraryDependencies ++= Seq(
         "org.scala-lang" % "scala-compiler" % scalaVersion.value,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )

lazy val macramePlay = Project("macrame-play", file("macrame-play"))
   .settings(
      version := "1.1.4-play-2.7.x-SNAPSHOT",
      publishTo := sonatypePublishTo.value,
      sonatypeProjectHosting := (Global / sonatypeProjectHosting).value,
      libraryDependencies ++= Seq(
         "com.typesafe.play" %% "play" % "[2.7,2.8[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )
   .dependsOn(macrame)

lazy val macrameScalaz = Project("macrame-scalaz", file("macrame-scalaz"))
   .settings(
      version := "1.0.4-scalaz-7.2.x-SNAPSHOT",
      publishTo := sonatypePublishTo.value,
      sonatypeProjectHosting := (Global / sonatypeProjectHosting).value,
      libraryDependencies ++= Seq(
         "org.scalaz" %% "scalaz-core" % "[7.2,7.3[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test,
         "org.scalacheck" %% "scalacheck" % "1.13.4" % Test)
   )
   .dependsOn(macrame)
