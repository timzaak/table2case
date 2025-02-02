package very.util.persistence.transfer

case class ScalasqlEntityParser(dialect: Dialect, entity: ScalaEntityParser) extends WriteToFile {

  override def `package`: String = entity.`package`

  override def name: String = entity.name

  def schema: String = {
    val fixedName = entity.copy(
      name = s"${entity.name}[T[_]]",
      imports = List("scalasql.*", s"scalasql.${dialect}Dialect.*") ::: entity.imports,
      fields = entity.fields.map((k, t) => (k, s"T[$t]")),
    )
    s"""${fixedName.schema}
       |object ${entity.name} extends Table[${entity.name}]""".stripMargin
  }

}

object ScalasqlEntityParser {
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
  ): ScalasqlEntityParser = {
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
    ScalasqlEntityParser(dialect, entity)
  }
}
