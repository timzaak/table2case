package very.util.persistence.transfer

import java.sql.JDBCType as JavaSqlTypes
import java.util.Locale

case class ScalaEntityParser(
  `package`: String,
  imports: List[String],
  name: String,
  `extends`: List[String],
  fields: List[(String, String)]
) extends WriteToFile {

  def schema: String = {
    val extendsStr =
      if (`extends`.nonEmpty) s" extends ${`extends`.mkString(" with ")}"
      else ""
    val importStr =
      if (imports.nonEmpty)
        s"\n${imports.map(v => s"import $v").mkString("", "\n", "\n")}"
      else ""
    s"""package ${`package`}
      |$importStr
      |case class $name(
      |${fields
        .map((field, typ) => s"$field: $typ,")
        .mkString("\n")})${extendsStr}
      |""".stripMargin
  }
}

object ScalaEntityParser {
  object TypeName {
    val Any = "Any"
    val AnyArray = "List[Any]"
    val ByteArray = "List[Byte]"
    val Long = "Long"
    val Boolean = "Boolean"
    val DateTime = "DateTime"
    val LocalDate = "LocalDate"
    val LocalTime = "LocalTime"
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
  case class ColumnInScala(underlying: Column) {

    lazy val typeInScala: String = {
      if (underlying.isNotNull) rawTypeInScala
      else "Option[" + rawTypeInScala + "]"
    }

    lazy val rawTypeInScala: String = underlying.dataType match {
      case JavaSqlTypes.ARRAY         => TypeName.AnyArray
      case JavaSqlTypes.BIGINT        => TypeName.Long
      case JavaSqlTypes.BINARY        => TypeName.ByteArray
      case JavaSqlTypes.BIT           => TypeName.Boolean
      case JavaSqlTypes.BLOB          => TypeName.Blob
      case JavaSqlTypes.BOOLEAN       => TypeName.Boolean
      case JavaSqlTypes.CHAR          => TypeName.String
      case JavaSqlTypes.CLOB          => TypeName.Clob
      case JavaSqlTypes.DATALINK      => TypeName.Any
      case JavaSqlTypes.DATE          => TypeName.LocalDate
      case JavaSqlTypes.DECIMAL       => TypeName.BigDecimal
      case JavaSqlTypes.DISTINCT      => TypeName.Any
      case JavaSqlTypes.DOUBLE        => TypeName.Double
      case JavaSqlTypes.FLOAT         => TypeName.Float
      case JavaSqlTypes.INTEGER       => TypeName.Int
      case JavaSqlTypes.JAVA_OBJECT   => TypeName.Any
      case JavaSqlTypes.LONGVARBINARY => TypeName.ByteArray
      case JavaSqlTypes.LONGVARCHAR   => TypeName.String
      case JavaSqlTypes.NULL          => TypeName.Any
      case JavaSqlTypes.NUMERIC       => TypeName.BigDecimal
      case JavaSqlTypes.OTHER         => TypeName.Any
      case JavaSqlTypes.REAL          => TypeName.Float
      case JavaSqlTypes.REF           => TypeName.Ref
      case JavaSqlTypes.SMALLINT      => TypeName.Short
      case JavaSqlTypes.STRUCT        => TypeName.Struct
      case JavaSqlTypes.TIME          => TypeName.LocalTime
      case JavaSqlTypes.TIMESTAMP =>
        TypeName.Any // config.dateTimeClass.simpleName
      case JavaSqlTypes.TINYINT      => TypeName.Byte
      case JavaSqlTypes.VARBINARY    => TypeName.ByteArray
      case JavaSqlTypes.VARCHAR      => TypeName.String
      case JavaSqlTypes.NVARCHAR     => TypeName.String
      case JavaSqlTypes.NCHAR        => TypeName.String
      case JavaSqlTypes.LONGNVARCHAR => TypeName.String
      case _                         => TypeName.Any
    }

    // private[CodeGenerator] def isAny: Boolean = rawTypeInScala == TypeName.Any
  }

  case class ColumnInScala2(underling: Column2) {
    lazy val typeInScala: String = {
      if (underlying.isNotNull) rawTypeInScala
      else "Option[" + rawTypeInScala + "]"
    }

    private def partialType(dataType: String): String = dataType match {
      case "text"                         => TypeName.String
      case v if v.startsWith("character") => TypeName.String
      case "integer"                      => TypeName.Int
      case "smallint"                     => TypeName.Short
      case "bigint"                       => TypeName.Long
      case "date"                         => TypeName.LocalDate
      case "bytea"                        => TypeName.ByteArray
      case "timestamp with time zone"     => TypeName.Any
      case "timestamp"                    => TypeName.Any
      case "boolean"                      => TypeName.Boolean
      case "jsonb" | "json"               => TypeName.Any
      case v if v.startsWith("numeric")   => TypeName.BigDecimal
      case v if v.endsWith("[]") =>
        s"List[${partialType(dataType.replaceFirst("\\[\\]", ""))}]"
    }

    lazy val rawTypeInScala: String = partialType(underling.dataType)
    
  }

  def fromTable2(
    table: Table2,
    `package`: String,
    `extends`: List[String] = List.empty,
    imports: List[String] = List.empty,
    nameF: String => String = toCamelCase.andThen(quoteReservedWord),
    columnNamePF: PartialFunction[String, String] = PartialFunction.empty,
    fieldTypePF: PartialFunction[String, String] = PartialFunction.empty,
  ): Unit = {
    val extendsFix = `extends`.map(extractImportsType)
    var _imports = imports ++ extendsFix.collect { case (Some(v), _) =>
      v
    }

    val fields = table.allColumns.map { column =>
      val columNameTransfer = lowerCamelCase.andThen(quoteReservedWord)
      val newName =
        if (columnNamePF.isDefinedAt(column.name)) columnNamePF(column.name)
        else columNameTransfer(column.name)
      val fixedColumn = ColumnInScala2(column.copy(name = newName))
      val fieldType = if (fieldTypePF.isDefinedAt(newName)) {
        val importType = fieldTypePF(newName)
        val (hasImport, typeName) = extractImportsType(importType)
        hasImport.foreach(v => _imports = _imports :+ v)
        if (!column.isNotNull) {
          s"Option[${typeName}]"
        } else {
          typeName
        }
      } else {
        fixedColumn.typeInScala
      }
      fixedColumn.underlying.name -> fieldType
    }
    _imports = _imports.distinct

    ScalaEntityParser(
      `package` = `package`,
      imports = _imports,
      name = nameF(table.name),
      `extends` = extendsFix.map(_._2),
      fields = fields
    )
  }

  def fromTable(
    table: Table,
    `package`: String,
    `extends`: List[String] = List.empty,
    imports: List[String] = List.empty,
    nameF: String => String = toCamelCase.andThen(quoteReservedWord),
    columnNamePF: PartialFunction[String, String] = PartialFunction.empty,
    fieldTypePF: PartialFunction[String, String] = PartialFunction.empty,
  ) = {
    val extendsFix = `extends`.map(extractImportsType)
    var _imports = imports ++ extendsFix.collect { case (Some(v), _) =>
      v
    }
    val fields = table.allColumns.map { column =>
      val columNameTransfer = lowerCamelCase.andThen(quoteReservedWord)
      val newName =
        if (columnNamePF.isDefinedAt(column.name)) columnNamePF(column.name)
        else columNameTransfer(column.name)
      val fixedColumn = ColumnInScala(column.copy(name = newName))
      val fieldType = if (fieldTypePF.isDefinedAt(newName)) {
        val importType = fieldTypePF(newName)
        val (hasImport, typeName) = extractImportsType(importType)
        hasImport.foreach(v => _imports = _imports :+ v)
        if (!column.isNotNull) {
          s"Option[${typeName}]"
        } else {
          typeName
        }
      } else {
        fixedColumn.typeInScala
      }
      fixedColumn.underlying.name -> fieldType
    }
    _imports = _imports.distinct

    ScalaEntityParser(
      `package` = `package`,
      imports = _imports,
      name = nameF(table.name),
      `extends` = extendsFix.map(_._2),
      fields = fields
    )
  }
}

def quoteReservedWord: String => String = { name =>
  if (reservedWords(name)) "`" + name + "`"
  else name
}

private val reservedWords: Set[String] = Set(
  "abstract",
  "case",
  "catch",
  "class",
  "def",
  "do",
  "else",
  "extends",
  "false",
  "final",
  "finally",
  "for",
  "forSome",
  "if",
  "implicit",
  "import",
  "lazy",
  "match",
  "new",
  "null",
  "macro",
  "object",
  "override",
  "package",
  "private",
  "protected",
  "return",
  "sealed",
  "super",
  "then",
  "this",
  "throw",
  "trait",
  "try",
  "true",
  "type",
  "val",
  "var",
  "while",
  "with",
  "yield"
)

def lowerCamelCase: String => String =
  toCamelCase.andThen { camelCase =>
    s"${camelCase.head.toLower}${camelCase.tail}"
  }

def toCamelCase: String => String = _.split("_").foldLeft("") {
  (camelCaseString, part) =>
    camelCaseString + toProperCase(part)
}
def toProperCase(s: String): String = {
  if (s == null || s.trim.isEmpty) ""
  else
    s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s
      .substring(1)
      .toLowerCase(Locale.ENGLISH)
}
def extractImportsType(s: String) = {
  val name = s.split("\\.").last
  (if (name == s) None else Some(s)) -> name
}
