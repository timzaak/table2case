package very.util.persistence.transfer.postgres

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.db.Dialect
import very.util.persistence.transfer.scala.ScalasqlEntityParser

class PostgreScalasqlEntityParserSuite extends FunSuite {

  test("Postgres scalasql Parse") {
    val table = PostgresHelper.getPGModel().allTables().find(_.name == "users").get
    val schema = ScalasqlEntityParser.fromTable(Dialect.Postgres, table, "com.timzaak.test").schema
    val expected =
      s"""package com.timzaak.test
         |
         |import scalasql.*
         |import scalasql.PostgresDialect.*
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
