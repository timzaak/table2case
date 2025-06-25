val scala3Version = "3.7.1"

Test / parallelExecution := false

lazy val root = project
  .in(file("."))
  .settings(
    name := "table2case",
    version := "0.0.2",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.xerial" % "sqlite-jdbc" % "3.50.1.0" % Test,
      "org.postgresql" % "postgresql" % "42.7.7" % Test,
      "mysql" % "mysql-connector-java" % "8.0.33" % Test,
      "org.scalameta" %% "munit" % "1.1.1" % Test,
      "org.testcontainers" % "postgresql" % "1.21.2" % Test,
      "org.testcontainers" % "mysql" % "1.21.2" % Test,
      "org.slf4j" % "slf4j-simple" % "2.0.17" % Test,
    ),
    organization := "com.fornetcode",
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/timzaak/table2case")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/timzaak/table2case"),
        "scm:git:https://github.com/timzaak/table2case.git"
      )
    ),
    developers := List(
      Developer(
        id = "timzaak",
        name = "timzaak",
        email = "zsy.evan@gmail.com",
        url = url("https://github.com/timzaak")
      )
    )
  )

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
