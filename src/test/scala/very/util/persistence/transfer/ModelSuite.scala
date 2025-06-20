package very.util.persistence.transfer

import munit.FunSuite

class ModelSuite extends FunSuite {

  test("SQLite model init") { // Ignored due to Model refactoring and missing SqliteTableInfo
    assert(SuiteHelper.getModel().allTables().nonEmpty)
  }

  // docker run --rm -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test -p 5432:5432 -d postgres:15
  // docker run --rm -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test -p 5432:5432 postgres:15
  test("PostgresSQL model Init".ignore) {
    SuiteHelper.getPGModel().allTables().nonEmpty
  }

  // Command to start MySQL docker:
  // docker run --rm --name mysql-test -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=testdb -p 3306:3306 -d mysql:8.0
  test("MySQL model Init") {
    SuiteHelper.getMySQLModel().allTables().nonEmpty
  }
}
