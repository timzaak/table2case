## TableToCaseClass

This is used to create case class code from Database info, it would support SQLite, Postgres and MySQL.

It's in early development, all things may change.

The startup code is from ScalikJDBC, this project made some changes to support other ORM project.


- [x] transfer to ScalaCaseClass
- [ ] support timestamp
- [x] support ScalaSQL
- [ ] add tests for Postgres and MySQL, add GitHub Actions.


##  Startup example
```scala
imort very.util.persistence.transfer.*

val model = Model("jdbc:postgresql://127.0.0.1/test", "postgres", "postgres")
model.listAllTables().foreach{table =>
  ScalasqlEntityParser.fromTable(Dialog.Postgres, table, "com.timzaak.test").writeToFile("./src/main/scala")
}
```

## Known Issue
- SQLite `timestamp` jdbc type would be `VARCHAR`.
- SQLite `primary key` would be nullable.