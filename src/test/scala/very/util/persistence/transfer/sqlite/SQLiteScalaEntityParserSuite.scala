package very.util.persistence.transfer.sqlite

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
import very.util.persistence.transfer.scala.ScalaEntityParser

class SQLiteScalaEntityParserSuite extends FunSuite {

  test("SQLite simpleEntity") {
    val table = SQLiteHelper.getModel().allTables().head
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

  test("mixture Test") {
    val table = SQLiteHelper.getModel().allTables().head
    val schema = ScalaEntityParser
      .fromTable(
        table,
        "com.timzaak.test",
        nameF = _ => "User",
        `extends` = List("very.util.Dao"),
        imports = List("scalikejdbc.*"),
        fieldTypePF = {
          case "info"     => "com.timzaak.entity.TestCase"
          case "username" => "NameString"
        }
      )
      .schema

    val expected =
      s"""package com.timzaak.test
         |
         |import scalikejdbc.*
         |import very.util.Dao
         |import com.timzaak.entity.TestCase
         |
         |case class User(
         |id: Int,
         |username: NameString,
         |info: Option[TestCase],
         |) extends Dao
         |""".stripMargin
    assertStringEquals(schema, expected)
  }

  test("entity with timestamp") {
    val table = SQLiteHelper.getModel(sql = SQLiteHelper.simpleSQLWithTime).allTables().find(_.name == "users").get
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    val expected = s"""package com.timzaak.test
                      |
                      |case class Users(
                      |id: Int,
                      |username: String,
                      |info: Option[String],
                      |createAt: Option[String],
                      |)
                      |""".stripMargin
    assertStringEquals(schema, expected)
  }
}
