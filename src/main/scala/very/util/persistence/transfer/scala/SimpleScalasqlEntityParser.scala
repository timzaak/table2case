package very.util.persistence.transfer.scala

import very.util.persistence.transfer.db.{ Dialect, Table }
import very.util.persistence.transfer.scala.{ ScalaEntityParser, quoteReservedWord, toCamelCase }
import very.util.persistence.transfer.util.WriteToFile

class SimpleScalasqlEntityParser(dialect: Dialect, entity: ScalaEntityParser) extends WriteToFile {

  override def `package`: String = entity.`package`

  override def name: String = entity.name

  def schema: String = {
    val fixedName = entity.copy(
      imports = List("scalasql.simple.*", s"scalasql.${dialect}Dialect.*") ::: entity.imports,
    )
    s"""${fixedName.schema}
       |object ${entity.name} extends SimpleTable[${entity.name}]""".stripMargin
  }

}

object SimpleScalasqlEntityParser {
  def fromTable(
    dialect: Dialect,
    table: Table,
    `package`: String,
    `extends`: List[String] = List.empty,
    imports: List[String] = List.empty,
    annotations: List[String] = List.empty,
    nameF: String => String = toCamelCase.andThen(quoteReservedWord),
    columnNamePF: PartialFunction[String, String] = PartialFunction.empty,
    fieldTypePF: PartialFunction[String, String] = PartialFunction.empty,
  ): SimpleScalasqlEntityParser = {
    val entity = ScalaEntityParser.fromTable(
      table,
      `package`,
      `extends`,
      imports,
      annotations,
      nameF,
      columnNamePF,
      fieldTypePF
    )
    SimpleScalasqlEntityParser(dialect, entity)
  }
}
