package very.util.persistence.transfer

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

  def getPGModel(sql: String = simplePGSQL): Model = {
    val model = Model("jdbc:postgresql://127.0.0.1/test", "postgres", "postgres")
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()
    model
  }

}
