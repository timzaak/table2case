package very.util.persistence.transfer.postgres

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.db.Dialect
import very.util.persistence.transfer.scala.SimpleScalasqlEntityParser

class PostgresSimpleScalasqlEntityParserSuite extends FunSuite {

  test("Postgres simple scalasql Parse") {
    val table = PostgresHelper.getPGModel().allTables().find(_.name == "users").get
    val schema = SimpleScalasqlEntityParser.fromTable(Dialect.Postgres, table, "com.timzaak.test").schema
    val expected =
      s"""package com.timzaak.test
         |
         |import scalasql.simple.*
         |import scalasql.PostgresDialect.*
         |
         |case class Users(
         |id: Int,
         |username: String,
         |info: Option[String],
         |)
         |
         |object Users extends SimpleTable[Users]""".stripMargin
    assertStringEquals(schema, expected)
  }
}
