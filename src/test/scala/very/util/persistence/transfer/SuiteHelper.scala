package very.util.persistence.transfer

import munit.Assertions.assertEquals

object SuiteHelper {

  inline def assertStringEquals(expected: String, actual: String): Unit = {
    assertEquals(expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"))
  }

}
