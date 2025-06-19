package very.util.persistence.transfer.wrapper

import java.sql.ResultSet

/**
 * scala.collection.Iterator object which wraps java.sql.ResultSet.
 */
class ResultSetIterator(rs: ResultSet) extends Iterator[WrappedResultSet] {

  private val cursor: ResultSetCursor = new ResultSetCursor(0)
  private var nextOpt: WrappedResultSet = null
  private var closed: Boolean = false

  override def hasNext: Boolean = {
    if (nextOpt != null) {
      true
    } else if (closed) {
      false
    } else if (rs.next) {
      cursor.position += 1
      nextOpt = WrappedResultSet(rs, cursor, cursor.position)
      true
    } else {
      rs.close()
      closed = true
      false
    }
  }

  override def next(): WrappedResultSet = {
    if (nextOpt != null) {
      val result = nextOpt
      nextOpt = null
      result
    } else if (closed) {
      Iterator.empty.next()
    } else if (rs.next) {
      cursor.position += 1
      WrappedResultSet(rs, cursor, cursor.position)
    } else {
      rs.close()
      closed = true
      Iterator.empty.next()
    }
  }
}
