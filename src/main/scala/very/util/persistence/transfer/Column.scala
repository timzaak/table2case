package very.util.persistence.transfer

import java.sql.JDBCType

case class Column(name: String, dataType: JDBCType, isNotNull: Boolean, isAutoIncrement: Boolean)


case class Column2(name:String, dataType:String, isNotNull: Boolean, isAutoIncrement: Boolean)