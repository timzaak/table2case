package very.util.persistence.transfer

import munit.FunSuite

class ScalasqlEntityParserSuite extends FunSuite {

  test("simple Scalasql Parse") {
    val table = SuiteHelper.getModel().allTables().head
    val schema = ScalasqlEntityParser.fromTable(Dialog.Sqlite, table, "com.timzaak.test").schema
    val expected =
      s"""package com.timzaak.test
         |
         |import scalasql.*
         |import scalasql.SqliteDialog.*
         |
         |case class Users[T[_]](
         |id: T[Option[Int]],
         |username: T[String],
         |info: T[Option[String]],)
         |
         |object Users extends Table[Users]
         |""".stripMargin
    assert(schema == expected)

  }
}
