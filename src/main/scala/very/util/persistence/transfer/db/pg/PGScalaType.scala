package very.util.persistence.transfer.db.pg

import very.util.persistence.transfer.db.pg.PGScalaType.partialType
import very.util.persistence.transfer.scala.{ ColumnScalaType, TypeName }

case class PGScalaType(dataType: String) extends ColumnScalaType {
  override lazy val rawType: String = partialType(dataType)
}

object PGScalaType {
  private def partialType(dataType: String): String = dataType match {
    case "text"                         => TypeName.String
    case v if v.startsWith("character") => TypeName.String
    case "integer"                      => TypeName.Int
    case "smallint"                     => TypeName.Short
    case "bigint"                       => TypeName.Long
    case "date"                         => TypeName.LocalDate
    case "bytea"                        => TypeName.ByteArray
    case "timestamp with time zone"     => TypeName.OffsetDateTime
    case "timestamp"                    => TypeName.LocalTime
    case "boolean"                      => TypeName.Boolean
    case "jsonb" | "json"               => TypeName.Any
    case v if v.startsWith("numeric")   => TypeName.BigDecimal
    case v if v.endsWith("[]")          =>
      s"List[${partialType(dataType.replaceFirst("\\[\\]", ""))}]"
  }
}
