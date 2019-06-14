import xerial.sbt.Sonatype._

sonatypeProfileName := "com.kinja"

publishMavenStyle := true
useGpg := true

Global / description := "Macrame provides macro-based replacements for parts of the Scala standard library."
Global / homepage := Some(url("https://github.com/gawkermedia/macrame"))
Global / licenses := Seq("BSD 3-Clause" -> url("https://github.com/gawkermedia/macrame/blob/master/LICENSE"))

Global / sonatypeProjectHosting := Some(GitHubHosting("gawkermedia", "macrame", ""))

Global / developers := List(
  Developer(id = "ClaireNeveu", name = "Claire Neveu", email = "", url = url("https://github.com/ClaireNeveu"))

)

Global / credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype")
pgpSecretRing := file(System.getProperty("SEC_RING", ""))
pgpPublicRing := file(System.getProperty("PUB_RING", ""))
pgpPassphrase := Some(Array(System.getProperty("PGP_PASS", ""): _*))
