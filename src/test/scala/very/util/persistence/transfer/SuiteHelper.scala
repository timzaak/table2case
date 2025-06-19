package very.util.persistence.transfer

import munit.Assertions.assertEquals
import org.testcontainers.containers.PostgreSQLContainer
import very.util.persistence.transfer.db.Model

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
    s"""create table if not exists users(
       |id serial primary key,
       |username text not null,
       |info text
       |)""".stripMargin

  lazy val postgres = {
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

  inline def assertStringEquals(expected: String, actual: String): Unit = {
    assertEquals(expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"))
  }

  def fullPGTypeSQL: String =
    s"""create table if not exists ta(
        |id serial primary key,
        |t_tiny_int int2,
        |t_long bigint,
        |t_bson jsonb,
        |t_date DATE ,
        |t_timestamp_tz timestamptz default now(),
        |t_text text,
        |t_money numeric(10,2),
        |t_array_int integer[],
        |t_array_text text[]
        |)
        |""".stripMargin
}
