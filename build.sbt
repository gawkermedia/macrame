import scalariform.formatter.preferences._

inThisBuild(Seq(
   organization := "com.kinja",
   organizationName := "Kinja",
   organizationHomepage := Some(url("https://kinja.com/")),

   scmInfo := Some(ScmInfo(
      url("https://github.com/gawkermedia/macrame"),
      "scm:git@github.com:gawkermedia/macrame.git"
   )),

   developers := List(Developer(
      id    = "claireneveu",
      name  = "Claire Neveu",
      email = "claireneveu@kinja.com",
      url   = url("https://kinja.com")
   )),

   description := "Macrame provides macro-based replacements for parts of the Scala standard library.",
   licenses := List("BSD 3-Clause" -> new URL("https://raw.githubusercontent.com/gawkermedia/macrame/master/LICENSE")),
   homepage := Some(url("https://github.com/gawkermedia/macrame")),

   scalaVersion := "2.12.8",
   crossScalaVersions := Seq("2.13.0-M5", "2.12.8", "2.11.12"),
   scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:higherKinds",
      "-language:postfixOps"
   ),

   credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype"),
   
   PgpKeys.useGpg := true,
   Global / PgpKeys.pgpSecretRing := file(System.getProperty("SEC_RING", "")),
   Global / PgpKeys.pgpPublicRing := file(System.getProperty("PUB_RING", "")),
   Global / PgpKeys.pgpPassphrase := Some(Array(System.getProperty("PGP_PASS", ""): _*)),
   
   scalariformPreferences := scalariformPreferences.value
      .setPreference(IndentSpaces, 3)
      .setPreference(SpaceBeforeColon, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
))

lazy val pubishingSettings = Seq(
   publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
         Some("snapshots" at nexus + "content/repositories/snapshots")
      else
         Some("releases" at nexus + "service/local/staging/deploy/maven2")
   }
)

lazy val root = Project("root", file("."))
   .settings(
      publishArtifact := false,
      publish / skip := true
   )
   .aggregate(macrame, macramePlay, macrameScalaz)

lazy val macrame = Project("macrame", file("macrame"))
   .settings(
      version := "1.2.9",
      scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq(
             "-Ymacro-annotations"
           )
         case x =>
           Seq()
      }),
      pubishingSettings,
      libraryDependencies ++= Seq(
         "org.scala-lang" % "scala-compiler" % scalaVersion.value,
         "org.scalatest" %% "scalatest" % "3.0.7" % Test),
      libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq()
         case _ =>
           Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
      })
   )

lazy val macramePlay = Project("macrame-play", file("macrame-play"))
   .settings(
      version := "1.1.3-play-2.7.x",
      scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq(
             "-Ymacro-annotations"
           )
         case x =>
           Seq()
      }),
      pubishingSettings,
      libraryDependencies ++= Seq(
         "com.typesafe.play" %% "play" % "[2.7,2.8[" % Provided,
         "org.scalatest" %% "scalatest" % "3.0.7" % Test),
      libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq()
         case _ =>
           Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
      })
   )
   .dependsOn(macrame)

lazy val macrameScalaz = Project("macrame-scalaz", file("macrame-scalaz"))
   .settings(
      version := "1.0.3-scalaz-7.2.x",
      scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq(
             "-Ymacro-annotations"
           )
         case x =>
           Seq()
      }),
      pubishingSettings,
      libraryDependencies ++= Seq(
         "org.scalaz" %% "scalaz-core" % "[7.2,7.3[" % Provided,
         "org.scalatest" %% "scalatest" % "3.0.7" % Test,
         "org.scalacheck" %% "scalacheck" % "1.14.0" % Test),
      libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
         case Some((2, scalaMajor)) if scalaMajor >= 13 =>
           Seq()
         case _ =>
           Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
      })
   )
   .dependsOn(macrame)
