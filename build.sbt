val scala3Version = "3.7.1"

Test / parallelExecution := false

lazy val root = project
  .in(file("."))
  .settings(
    name := "table2case",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.xerial" % "sqlite-jdbc" % "3.50.1.0" % Test,
      "org.postgresql" % "postgresql" % "42.7.7" % Test,
      "mysql" % "mysql-connector-java" % "8.0.33" % Test,
      "org.scalameta" %% "munit" % "1.1.1" % Test,
      "org.testcontainers" % "postgresql" % "1.21.2" % Test,
      "org.testcontainers" % "mysql" % "1.21.2" % Test,
      "org.slf4j" % "slf4j-simple" % "2.0.17" % Test,
    )
  )
