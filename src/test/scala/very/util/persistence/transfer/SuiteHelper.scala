package very.util.persistence.transfer

import munit.Assertions.assertEquals
import org.testcontainers.containers.{ MySQLContainer, PostgreSQLContainer }
import org.testcontainers.utility.DockerImageName
import very.util.persistence.transfer.db.{ Dialect, Model }

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
    // Assuming SQLite connection, so user/pass are null. Dialect is Sqlite.
    val model = Model(driver, null, null)
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

  // Define mysqlContainer as a lazy val
  private lazy val mysqlContainer = {

    val container = new MySQLContainer(DockerImageName.parse("mysql:8.0.33"))
    container.start()
    container
  }

  def simpleMySQL: String =
    s"""CREATE TABLE IF NOT EXISTS users (
       |id INT PRIMARY KEY AUTO_INCREMENT,
       |username VARCHAR(255) NOT NULL,
       |info TEXT
       |)""".stripMargin

  def getMySQLModel(sql: String = simpleMySQL): Model = {
    Class.forName("com.mysql.cj.jdbc.Driver")

    val model = Model(
      mysqlContainer.getJdbcUrl(),
      mysqlContainer.getUsername(),
      mysqlContainer.getPassword(),
    )

    val stmt = model._connection.createStatement()
    try {
      stmt.execute(sql)
    } finally {
      stmt.close()
    }
    model
  }

  def getPGModel(sql: String = simplePGSQL): Model = {
    val model = Model(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
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
        |t_bson jsonb, // Postgres specific
        |t_date DATE ,
        |t_timestamp_tz timestamptz default now(), // Postgres specific
        |t_text text,
        |t_money numeric(10,2),
        |t_array_int integer[], // Postgres specific
        |t_array_text text[] // Postgres specific
        |)
        |""".stripMargin

  def fullMySQLTypeSQL: String =
    s"""CREATE TABLE IF NOT EXISTS ta(
        |id INT AUTO_INCREMENT PRIMARY KEY,
        |t_tiny_int TINYINT, -- MySQL equivalent for int2 (smallint)
        |t_long BIGINT,
        |t_bson JSON, -- MySQL uses JSON type
        |t_date DATE,
        |t_timestamp_tz TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- MySQL equivalent
        |t_text TEXT,
        |t_money DECIMAL(10,2), -- MySQL equivalent
        |t_array_int JSON, -- Workaround for array types using JSON
        |t_array_text JSON -- Workaround for array types using JSON
        |)
        |""".stripMargin
}
