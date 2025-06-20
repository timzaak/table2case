package very.util.persistence.transfer.db.mysql

import very.util.persistence.transfer.scala.{ ColumnScalaType, TypeName }

case class MySQLScalaType(dataType: String) extends ColumnScalaType {
  override lazy val rawType: String = MySQLScalaType.partialType(dataType)
}

object MySQLScalaType {
  private def partialType(dataType: String): String = dataType.toLowerCase match {
    case "text" | "varchar" | "char" | "tinytext" | "mediumtext" | "longtext" | "enum" | "set" => TypeName.String
    case "int" | "integer" | "mediumint"                                                       => TypeName.Int
    case "smallint"                                                                            => TypeName.Short
    case "bigint"                                                                              => TypeName.Long
    case "date"                                                                                => TypeName.LocalDate
    case "datetime" | "timestamp"                                                              => TypeName.LocalDateTime
    case "time"                                                                                => TypeName.LocalTime
    case "year"                                                                                => TypeName.Short
    case "bit" | "boolean" | "tinyint(1)"                                                      => TypeName.Boolean
    case "tinyint"                                                                             => TypeName.Byte
    case "blob" | "tinyblob" | "mediumblob" | "longblob" | "binary" | "varbinary"              => TypeName.ByteArray
    case "float" | "real"                                                                      => TypeName.Float
    case "double" | "double precision"                                                         => TypeName.Double
    case "decimal" | "numeric" | "dec"                                                         => TypeName.BigDecimal
    case "json"                                                                                => TypeName.String
    case _                                                                                     => TypeName.Any
  }
}
