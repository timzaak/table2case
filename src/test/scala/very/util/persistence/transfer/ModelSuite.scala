package very.util.persistence.transfer

import munit.FunSuite

class ModelSuite extends FunSuite {

  override def beforeAll(): Unit = {


  }

  test("model init") {
    val model = Model("jdbc:sqlite::memory:")
    val sql = s"""create table if not exists users(
       |id INTEGER PRIMARY KEY,
       |username TEXT NOT NULL,
       |info TEXT
       |)""".stripMargin
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()

    println(model.allTables())
  }

}
