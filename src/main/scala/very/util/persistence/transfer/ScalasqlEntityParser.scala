package very.util.persistence.transfer

case class ScalasqlEntityParser(dialog: Dialog, entity: ScalaEntityParser) extends WriteToFile {

  override def `package`: String = entity.`package`
  
  def schema: String = {
    val fixedName = entity.copy(
      name = s"${entity.name}[T[_]]",
      imports =
        List("scalasql.*", s"scalasql.${dialog}Dialog.*") ::: entity.imports,
      fields = entity.fields.map((k, t) => (k, s"T[$t]")),
    )
    s"""${fixedName.schema}
       |object ${entity.name} extends Table[${entity.name}]
       |""".stripMargin
  }

}

object ScalasqlEntityParser {
  def fromTable(
    dialog: Dialog,
    table: Table,
    `package`: String,
    `extends`: List[String] = List.empty,
    imports: List[String] = List.empty,
    nameF: String => String = toCamelCase.andThen(quoteReservedWord),
    columnNamePF: PartialFunction[String, String] = PartialFunction.empty,
    fieldTypePF: PartialFunction[String, String] = PartialFunction.empty,
  ): ScalasqlEntityParser = {
    val entity = ScalaEntityParser.fromTable(
      table,
      `package`,
      `extends`,
      imports,
      nameF,
      columnNamePF,
      fieldTypePF
    )
    ScalasqlEntityParser(dialog, entity)
  }
}
