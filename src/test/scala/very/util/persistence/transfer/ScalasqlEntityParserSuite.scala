package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.db.Dialect
import very.util.persistence.transfer.scala.ScalasqlEntityParser

class ScalasqlEntityParserSuite extends FunSuite {

  test("simple Scalasql Parse") {
    val table = SuiteHelper.getModel().allTables().head
    val schema = ScalasqlEntityParser.fromTable(Dialect.Sqlite, table, "com.timzaak.test").schema
    val expected =
      s"""package com.timzaak.test
         |
         |import scalasql.*
         |import scalasql.SqliteDialect.*
         |
         |case class Users[T[_]](
         |id: T[Option[Int]],
         |username: T[String],
         |info: T[Option[String]],
         |)
         |
         |object Users extends Table[Users]""".stripMargin
    assertStringEquals(schema, expected)

  }

  test("MySQL Scalasql Parse") {
    val table = SuiteHelper.getMySQLModel().allTables().find(_.name == "users").get
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
