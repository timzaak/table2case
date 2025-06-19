package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.db.pg.PGTableInfo

class PGInfoSuite extends FunSuite {
  test("simple") {
    val model = SuiteHelper.getPGModel(SuiteHelper.fullPGTypeSQL)
    val info = PGTableInfo.getTableInfo(model._connection)
    assert(info.isSuccess)
  }

}
