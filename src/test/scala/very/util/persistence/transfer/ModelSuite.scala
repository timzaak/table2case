package very.util.persistence.transfer

import munit.FunSuite

class ModelSuite extends FunSuite {

  test("SQLite model init") {
    assert(SuiteHelper.getModel().allTables().nonEmpty)
  }

  // docker run --rm -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test -p 5432:5432 -d postgres:15
  // docker run --rm -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test -p 5432:5432 postgres:15
  test("PostgresSQL model Init".ignore) {
    SuiteHelper.getPGModel().allTables().nonEmpty
  }

}
