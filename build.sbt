val scala3Version = "3.6.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "table2case",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      // "nl.big-o" % "liqp" % "0.9.1.0",
      "org.xerial" % "sqlite-jdbc" % "3.49.1.0" % Test,
      "org.postgresql" % "postgresql" % "42.7.5" % Test,
      "org.scalameta" %% "munit" % "1.1.0" % Test,
      "org.testcontainers" % "postgresql" % "1.20.5" % Test,
      "org.slf4j" % "slf4j-simple" % "2.0.16" % Test,
    )
  )
