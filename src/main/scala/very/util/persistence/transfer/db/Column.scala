package very.util.persistence.transfer.db

import java.sql.JDBCType as JavaSqlTypes

trait Column {
  def name: String
  def isNotNull: Boolean
  def isAutoIncrement: Boolean
}

case class JDBCColumn(name: String, dataType: JavaSqlTypes, isNotNull: Boolean, isAutoIncrement: Boolean) extends Column

case class SQLColumn(
  name: String,
  dataType: String,
  isNotNull: Boolean,
  isAutoIncrement: Boolean,
) extends Column
