package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.mysql.MySQLHelper
import very.util.persistence.transfer.postgres.PostgresHelper
import very.util.persistence.transfer.sqlite.SQLiteHelper

class ModelSuite extends FunSuite {

  test("SQLite model init") {
    assert(SQLiteHelper.getModel().allTables().nonEmpty)
  }

  test("PostgresSQL model Init".ignore) {
    assert(PostgresHelper.getPGModel().allTables().nonEmpty)
  }

  test("MySQL model Init") {
    assert(MySQLHelper.getMySQLModel().allTables().nonEmpty)
  }
}
