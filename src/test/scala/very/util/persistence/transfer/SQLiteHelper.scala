package very.util.persistence.transfer

import very.util.persistence.transfer.db.Model

object SQLiteHelper {

  def simpleSQL: String =
    """create table if not exists users(
       |id INTEGER PRIMARY KEY,
       |username TEXT NOT NULL,
       |info TEXT
       |)
       """.stripMargin

  def simpleSQLWithTime: String =
    """create table if not exists users(
       |id INTEGER PRIMARY KEY,
       |username TEXT NOT NULL,
       |info TEXT,
       |create_at DATE DEFAULT (datetime('now','localtime'))
       |)
       """.stripMargin

  def getModel(
    driver: String = "jdbc:sqlite::memory:",
    sql: String = simpleSQL
  ): Model = {
    val model = Model(driver, null, null)
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()
    model
  }
}
