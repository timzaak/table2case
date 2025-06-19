package very.util.persistence.transfer.wrapper

import java.sql.ResultSet
import scala.jdk.CollectionConverters.*

/**
 * java.sql.ResultSet wrapper.
 */
case class WrappedResultSet(
  underlying: ResultSet,
  cursor: ResultSetCursor,
  index: Int
) {
  def ensureCursor(): Unit = {
    if (cursor.position != index) {
      throw new IllegalStateException(
        "Invalid cursor (actual:" + cursor.position + ",expected:" + index + ")"
      )
    }
  }

  def string(columnLabel: String): String = {
    ensureCursor()
    underlying.getString(columnLabel)
  }

  def concurrency: Int = {
    ensureCursor()
    underlying.getConcurrency
  }

  def cursorName: String = {
    ensureCursor()
    underlying.getCursorName
  }

}
