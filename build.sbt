val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "table2case",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "nl.big-o"% "liqp" % "0.9.1.0",
      "org.xerial" % "sqlite-jdbc" % "3.46.1.0" % Test,
      "org.scalameta" %% "munit" % "1.0.0" % Test,
    )
  )
