package very.util.persistence.transfer.mysql

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper
import very.util.persistence.transfer.SuiteHelper.assertStringEquals // Ensure this import is present
import very.util.persistence.transfer.scala.ScalaEntityParser

class MySQLScalaEntityParserSuite extends FunSuite {

  test("MySQL simpleEntity") {
    val allTable = SuiteHelper.getMySQLModel().allTables()
    // println(allTable) // Commented out println
    val table = allTable.find(_.name == "users").get
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    val expected = s"""package com.timzaak.test
                      |
                      |case class Users(
                      |id: Int,
                      |username: String,
                      |info: Option[String],
                      |)
                      |""".stripMargin
    assertStringEquals(schema, expected)
  }
}
