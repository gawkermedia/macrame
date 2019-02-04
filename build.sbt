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
   crossScalaVersions := Seq("2.12.8", "2.11.8"),
   scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:higherKinds",
      "-language:postfixOps"
   ),
   
   credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype"),
   
   useGpg := true,
   Global / pgpSecretRing := file(System.getProperty("SEC_RING", "")),
   Global / pgpPublicRing := file(System.getProperty("PUB_RING", "")),
   Global / pgpPassphrase := Some(Array(System.getProperty("PGP_PASS", ""): _*)),
   
   scalariformPreferences := scalariformPreferences.value
      .setPreference(IndentSpaces, 3)
      .setPreference(SpaceBeforeColon, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
))

lazy val pubishingSettings = Seq(
   publishTo := {
      val nexus = "https://my.artifact.repo.net/"
      if (isSnapshot.value)
         Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
         Some("releases"  at nexus + "service/local/staging/deploy/maven2")
   }
)

lazy val root = Project("root", file("."))
   .settings(publishArtifact := false)
   .aggregate(macrame, macramePlay, macrameScalaz)

lazy val macrame = Project("macrame", file("macrame"))
   .settings(
      version := "1.2.9-SNAPSHOT",
      pubishingSettings,
      libraryDependencies ++= Seq(
         "org.scala-lang" % "scala-compiler" % scalaVersion.value,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )

lazy val macramePlay = Project("macrame-play", file("macrame-play"))
   .settings(
      version := "1.1.3-play-2.7.x-SNAPSHOT",
      pubishingSettings,
      libraryDependencies ++= Seq(
         "com.typesafe.play" %% "play" % "[2.7,2.8[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test)
   )
   .dependsOn(macrame)

lazy val macrameScalaz = Project("macrame-scalaz", file("macrame-scalaz"))
   .settings(
      version := "1.0.3-scalaz-7.2.x-SNAPSHOT",
      pubishingSettings,
      libraryDependencies ++= Seq(
         "org.scalaz" %% "scalaz-core" % "[7.2,7.3[" % Provided,
         compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % Test cross CrossVersion.full),
         "org.scalatest" %% "scalatest" % "3.0.0" % Test,
         "org.scalacheck" %% "scalacheck" % "1.13.4" % Test)
   )
   .dependsOn(macrame)
