package very.util.persistence.transfer

import munit.FunSuite

class ModelSuite extends FunSuite {

  test("SQLite model init") {
    println(SuiteHelper.getModel().allTables())
  }

}
