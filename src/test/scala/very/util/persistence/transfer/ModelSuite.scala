package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.SQLiteHelper
import very.util.persistence.transfer.PostgresHelper
import very.util.persistence.transfer.MySQLHelper

class ModelSuite extends FunSuite {

  test("SQLite model init") {
    assert(SQLiteHelper.getModel().allTables().nonEmpty)
  }

  test("PostgresSQL model Init".ignore) {
    PostgresHelper.getPGModel().allTables().nonEmpty
  }

  test("MySQL model Init") {
    MySQLHelper.getMySQLModel().allTables().nonEmpty
  }
}
