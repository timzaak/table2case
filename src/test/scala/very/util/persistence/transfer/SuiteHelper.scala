package very.util.persistence.transfer

import munit.Assertions.assertEquals
import org.testcontainers.containers.PostgreSQLContainer

object SuiteHelper {

  def simpleSQL: String =
    s"""create table if not exists users(
       |id INTEGER PRIMARY KEY,
       |username TEXT NOT NULL,
       |info TEXT
       |)""".stripMargin

  def getModel(
    driver: String = "jdbc:sqlite::memory:",
    sql: String = simpleSQL
  ): Model = {
    val model = Model(driver)
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()
    model
  }

  def simpleSQLWithTime: String =
    s"""create table if not exists users(
       |id INTEGER PRIMARY KEY,
       |username TEXT NOT NULL,
       |info TEXT,
       |create_at DATE DEFAULT (datetime('now','localtime'))
       |)""".stripMargin

  def simplePGSQL: String =
    s"""create table if not exists Users(
       |id serial primary key,
       |username text not null,
       |info text
       |)""".stripMargin

  lazy val postgres = {
    println("Initializing Postgres")
    val pg = new PostgreSQLContainer("postgres:15")
    pg.start()
    pg
  }
  def getPGModel(sql: String = simplePGSQL): Model = {
    val model = Model(s"${postgres.getJdbcUrl}/${postgres.getDatabaseName}", postgres.getUsername, postgres.getPassword)
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()
    model
  }
  
  
  def assertStringEquals(expected: String, actual: String): Unit = {
    assertEquals(expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"))
  }

}
