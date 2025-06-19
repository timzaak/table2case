package very.util.persistence.transfer.db

import very.util.persistence.transfer.db.{ JDBCColumn, SQLColumn }

trait Table {
  def name: String
  def allColumns: List[Column]
  def primaryKeyColumns: List[String]
  def schema: Option[String]
}

case class JDBCTable(
  name: String,
  allColumns: List[JDBCColumn],
  primaryKeyColumns: List[String],
  schema: Option[String] = None
) extends Table

case class SQLTable(
  name: String,
  allColumns: List[SQLColumn],
  primaryKeyColumns: List[String],
  schema: Option[String] = None
) extends Table
