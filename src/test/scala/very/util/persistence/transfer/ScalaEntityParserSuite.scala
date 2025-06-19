package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals

class ScalaEntityParserSuite extends FunSuite {

  test("SQLite simpleEntity") {
    val table = SuiteHelper.getModel().allTables().head
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    val expected = s"""package com.timzaak.test
                      |
                      |case class Users(
                      |id: Option[Int],
                      |username: String,
                      |info: Option[String],
                      |)
                      |""".stripMargin
    assertStringEquals(schema, expected)
  }
  test("PostgreSQL simpleEntity") {
    val table = SuiteHelper.getPGModel().allTables().head
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

  test("mixture Test") {
    val table = SuiteHelper.getModel().allTables().head
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
         |id: Option[Int],
         |username: NameString,
         |info: Option[TestCase],
         |) extends Dao
         |""".stripMargin
    assertStringEquals(schema, expected)
  }

  // timestamp for SQLite is string type
  test("entity with timestamp") {
    val table = SuiteHelper.getModel(sql = SuiteHelper.simpleSQLWithTime).allTables().head
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    val expected = s"""package com.timzaak.test
                      |
                      |case class Users(
                      |id: Option[Int],
                      |username: String,
                      |info: Option[String],
                      |createAt: Option[String],
                      |)
                      |""".stripMargin
    assertStringEquals(schema, expected)
  }
}
