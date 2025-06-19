package very.util.persistence.transfer.scala

import very.util.persistence.transfer.db.{ JDBCTable, Table }
import very.util.persistence.transfer.util.WriteToFile

import java.util.Locale

case class ScalaEntityParser(
  `package`: String,
  imports: List[String],
  name: String,
  `extends`: List[String],
  annotations: List[String],
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
      |$importStr${annotations.mkString("\n")}
      |case class $name(
      |${fields.map((field, typ) => s"$field: $typ,").mkString("\n")}
      |)${extendsStr}
      |""".stripMargin
  }
}

object ScalaEntityParser {

  def fromTable(
    table: Table,
    `package`: String,
    `extends`: List[String] = List.empty,
    imports: List[String] = List.empty,
    annotations: List[String] = List.empty,
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
      val scalaType = ColumnScalaType.from(column)
      val fieldType = if (fieldTypePF.isDefinedAt(newName)) {
        val importType = fieldTypePF(newName)
        val (hasImport, typeName) = extractImportsType(importType)
        hasImport.foreach(v => _imports = _imports :+ v)
        if (!column.isNotNull) {
          s"Option[$typeName]"
        } else {
          typeName
        }
      } else {
        if (
          Set(TypeName.OffsetDateTime, TypeName.DateTime, TypeName.LocalTime)
            .contains(scalaType.rawType)
        ) {
          _imports = _imports :+ s"java.time.${scalaType.rawType}"
        }

        scalaType.realType(column.isNotNull)
      }
      newName -> fieldType
    }
    _imports = _imports.distinct

    ScalaEntityParser(
      `package` = `package`,
      imports = _imports,
      name = nameF(table.name),
      `extends` = extendsFix.map(_._2),
      annotations = annotations,
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

def toCamelCase: String => String = _.split("_").foldLeft("") { (camelCaseString, part) =>
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
