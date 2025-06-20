package very.util.persistence.transfer.mysql

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.MySQLHelper
import very.util.persistence.transfer.db.Dialect
import very.util.persistence.transfer.scala.ScalasqlEntityParser

class MySQLScalasqlEntityParserSuite extends FunSuite {

  test("MySQL Scalasql Parse") {
    val table = MySQLHelper.getMySQLModel().allTables().find(_.name == "users").get
    val schema = ScalasqlEntityParser.fromTable(Dialect.MySql, table, "com.timzaak.test").schema
    val expected =
      s"""package com.timzaak.test
         |
         |import scalasql.*
         |import scalasql.MySqlDialect.*
         |
         |case class Users[T[_]](
         |id: T[Int],
         |username: T[String],
         |info: T[Option[String]],
         |)
         |
         |object Users extends Table[Users]""".stripMargin
    assertStringEquals(schema, expected)
  }
}
