package very.util.persistence.transfer.db

import very.util.persistence.transfer.db.{ JDBCColumn, SQLColumn }

trait Table {
  def name: String
  def dialect: Dialect
  def allColumns: List[Column]
  def primaryKeyColumns: List[String]
  def schema: Option[String]
}

case class JDBCTable(
  name: String,
  dialect: Dialect,
  allColumns: List[JDBCColumn],
  primaryKeyColumns: List[String],
  schema: Option[String] = None
) extends Table

case class SQLTable(
  name: String,
  dialect: Dialect,
  allColumns: List[SQLColumn],
  primaryKeyColumns: List[String],
  schema: Option[String] = None
) extends Table
