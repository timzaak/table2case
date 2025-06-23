package very.util.persistence.transfer.postgres

import munit.FunSuite
import very.util.persistence.transfer.SuiteHelper.assertStringEquals
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

  test("fullEntity") {
    val model = PostgresHelper.getPGModel(PostgresHelper.fullPGTypeSQL).allTables()
    val table = model.find(_.name == "ta").get
    val schema = ScalaEntityParser.fromTable(table, "com.timzaak.test").schema
    val expected = s"""package com.timzaak.test
                      |
                      |import java.time.OffsetDateTime
                      |
                      |case class Ta(
                      |id: Int,
                      |tTinyInt: Option[Short],
                      |tLong: Option[Long],
                      |tBson: Option[Any],
                      |tDate: Option[LocalDate],
                      |tTimestampTz: Option[OffsetDateTime],
                      |tText: Option[String],
                      |tMoney: Option[BigDecimal],
                      |tArrayInt: Option[List[Int]],
                      |tArrayText: Option[List[String]],
                      |)
                      |""".stripMargin
    assertStringEquals(schema, expected)
  }
}
