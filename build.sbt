import scalariform.formatter.preferences._

lazy val pomStuff = {
  <url>https://github.com/gawkermedia/macrame</url>
  <licenses>
   <license>
     <name>BSD 3-Clause</name>
     <url>https://raw.githubusercontent.com/gawkermedia/macrame/master/LICENSE</url>
   </license>
  </licenses>
  <scm>
   <connection>scm:git:github.com/gawkermedia/macrame.git</connection>
   <developerConnection>scm:git:git@github.com:gawkermedia/macrame.git</developerConnection>
   <url>git@github.com:gawkermedia/macrame.git</url>
  </scm>
  <developers>
   <developer>
     <name>Claire Neveu</name>
     <url>kinja.com</url>
   </developer>
  </developers>
}

inThisBuild(Seq(
   organization := "com.kinja",
   scalaVersion := "2.12.8",
   crossScalaVersions := Seq("2.12.8", "2.11.8"),
   scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:higherKinds",
      "-language:postfixOps"
   ),
   // Publishing
   PgpKeys.useGpg := true,
   credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype"),
   PgpKeys.pgpSecretRing in Global := file(System.getProperty("SEC_RING", "")),
   PgpKeys.pgpPublicRing in Global := file(System.getProperty("PUB_RING", "")),
   PgpKeys.pgpPassphrase in Global := Some(Array(System.getProperty("PGP_PASS", ""): _*)),
   pomExtra := pomStuff,
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
   .settings(publishArtifact := false)
   .aggregate(macrame, macramePlay, macrameScalaz)

lazy val macrame = Project("macrame", file("macrame"))
   .settings(
      version := "1.2.9",
      libraryDependencies ++= Seq(
         "org.scala-lang" % "scala-compiler" % scalaVersion.value,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )

lazy val macramePlay = Project("macrame-play", file("macrame-play"))
   .settings(
      version := "1.1.3-play-2.7.x",
      libraryDependencies ++= Seq(
         "com.typesafe.play" %% "play" % "[2.7,2.8[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )
   .dependsOn(macrame)

lazy val macrameScalaz = Project("macrame-scalaz", file("macrame-scalaz"))
   .settings(
      version := "1.0.3-scalaz-7.2.x",
      libraryDependencies ++= Seq(
         "org.scalaz" %% "scalaz-core" % "[7.2,7.3[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test,
         "org.scalacheck" %% "scalacheck" % "1.13.4" % Test)
   )
   .dependsOn(macrame)
