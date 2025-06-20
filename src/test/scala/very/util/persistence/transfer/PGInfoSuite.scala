package very.util.persistence.transfer

import munit.FunSuite
import very.util.persistence.transfer.db.pg.PGTableInfo
import very.util.persistence.transfer.PostgresHelper

class PGInfoSuite extends FunSuite {
  test("simple".ignore) {
    val model = PostgresHelper.getPGModel(PostgresHelper.fullPGTypeSQL)
    val info = PGTableInfo.getTableInfo(model._connection)
    assert(info.isSuccess)
  }
}
