package very.util.persistence.transfer.scala

import very.util.persistence.transfer.db.Dialect.{ MySql, Postgres }
import very.util.persistence.transfer.db.mysql.MySQLScalaType
import very.util.persistence.transfer.db.pg.PGScalaType
import very.util.persistence.transfer.db.{ Column, Dialect, JDBCColumn, SQLColumn }

import java.sql.JDBCType as JavaSqlTypes

trait ColumnScalaType {
  def rawType: String

  def realType(isNotNull: Boolean): String = if (isNotNull) rawType else "Option[" + rawType + "]"
}

object ColumnScalaType {
  def from(column: Column, dialect: Dialect): ColumnScalaType = {
    column match {
      case jdbcColumn: JDBCColumn                      => JDBCScalaType(jdbcColumn.dataType)
      case sqlColumn: SQLColumn if dialect == Postgres => PGScalaType(sqlColumn.dataType)
      case sqlColumn: SQLColumn if dialect == MySql    => MySQLScalaType(sqlColumn.dataType)
    }
  }
}

case class JDBCScalaType(rawType: String) extends ColumnScalaType

object JDBCScalaType {
  def apply(jdbcType: JavaSqlTypes): JDBCScalaType = {
    val rawTypeInScala: String = jdbcType match {
      case JavaSqlTypes.ARRAY                   => TypeName.AnyArray
      case JavaSqlTypes.BIGINT                  => TypeName.Long
      case JavaSqlTypes.BINARY                  => TypeName.ByteArray
      case JavaSqlTypes.BIT                     => TypeName.Boolean
      case JavaSqlTypes.BLOB                    => TypeName.Blob
      case JavaSqlTypes.BOOLEAN                 => TypeName.Boolean
      case JavaSqlTypes.CHAR                    => TypeName.String
      case JavaSqlTypes.CLOB                    => TypeName.Clob
      case JavaSqlTypes.DATALINK                => TypeName.Any
      case JavaSqlTypes.DATE                    => TypeName.LocalDate
      case JavaSqlTypes.DECIMAL                 => TypeName.BigDecimal
      case JavaSqlTypes.DISTINCT                => TypeName.Any
      case JavaSqlTypes.DOUBLE                  => TypeName.Double
      case JavaSqlTypes.FLOAT                   => TypeName.Float
      case JavaSqlTypes.INTEGER                 => TypeName.Int
      case JavaSqlTypes.JAVA_OBJECT             => TypeName.Any
      case JavaSqlTypes.LONGVARBINARY           => TypeName.ByteArray
      case JavaSqlTypes.LONGVARCHAR             => TypeName.String
      case JavaSqlTypes.NULL                    => TypeName.Any
      case JavaSqlTypes.NUMERIC                 => TypeName.BigDecimal
      case JavaSqlTypes.OTHER                   => TypeName.Any
      case JavaSqlTypes.REAL                    => TypeName.Float
      case JavaSqlTypes.REF                     => TypeName.Ref
      case JavaSqlTypes.SMALLINT                => TypeName.Short
      case JavaSqlTypes.STRUCT                  => TypeName.Struct
      case JavaSqlTypes.TIME                    => TypeName.LocalTime
      case JavaSqlTypes.TIMESTAMP               => TypeName.LocalTime
      case JavaSqlTypes.TIMESTAMP_WITH_TIMEZONE => TypeName.OffsetDateTime
      case JavaSqlTypes.TINYINT                 => TypeName.Byte
      case JavaSqlTypes.VARBINARY               => TypeName.ByteArray
      case JavaSqlTypes.VARCHAR                 => TypeName.String
      case JavaSqlTypes.NVARCHAR                => TypeName.String
      case JavaSqlTypes.NCHAR                   => TypeName.String
      case JavaSqlTypes.LONGNVARCHAR            => TypeName.String
      case _                                    => TypeName.Any
    }
    JDBCScalaType(rawTypeInScala)
  }
}

object TypeName {
  val Any = "Any"
  val LocalDateTime = "LocalDateTime" // Added this line
  val AnyArray = "List[Any]"
  val ByteArray = "List[Byte]"
  val Long = "Long"
  val Boolean = "Boolean"
  val DateTime = "DateTime"
  val LocalDate = "LocalDate"
  val LocalTime = "LocalTime"
  val OffsetDateTime = "OffsetDateTime"
  val String = "String"
  val Byte = "Byte"
  val Int = "Int"
  val Short = "Short"
  val Float = "Float"
  val Double = "Double"
  val Blob = "Blob"
  val Clob = "Clob"
  val Ref = "Ref"
  val Struct = "Struct"
  val BigDecimal = "BigDecimal" // scala.math.BigDecimal
}
