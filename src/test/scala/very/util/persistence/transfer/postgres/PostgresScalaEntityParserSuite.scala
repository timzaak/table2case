package very.util.persistence.transfer.postgres

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.PostgresHelper
import very.util.persistence.transfer.scala.ScalaEntityParser

class PostgresScalaEntityParserSuite extends FunSuite {

  test("PostgresSQL simpleEntity") {
    val allTable = PostgresHelper.getPGModel().allTables()
    val table = allTable.find(_.name == "users").get
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    assertStringEquals(
      schema,
      s"""package com.timzaak.test
        |
        |case class Users(
        |id: Int,
        |username: String,
        |info: Option[String],
        |)
        |""".stripMargin
    )
  }
}
