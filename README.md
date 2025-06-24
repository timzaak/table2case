## TableToCaseClass

[![Maven Central](https://img.shields.io/maven-central/v/com.timzaak/table2case_3.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.timzaak/table2case_3)

This is used to create case class code from Database info, it would support SQLite, Postgres and MySQL.

It's in early development, all things may change.

The startup code is from ScalikJDBC, this project made some changes to support other ORM project.

- [x] transfer to ScalaCaseClass
- [x] support java.time.*
- [x] support ScalaSQL
- [x] add tests for Postgres and MySQL, add GitHub Actions.
- [ ] reformat code by scalafmt

## Startup example

create table sql:

```postgresql
create table if not exists ta
(
    id             serial primary key,
    t_tiny_int     int2,
    t_long         bigint,
    t_bson         jsonb,
    t_date         DATE,
    t_timestamp_tz timestamptz default now(),
    t_text         text,
    t_money        numeric(10, 2),
    t_array_int    integer[],
    t_array_text   text[]
);
```

```scala
import very.util.persistence.transfer.db.Model
import very.util.persistence.transfer.scala.ScalaEntityParser

val model = Model("jdbc:postgresql://127.0.0.1/test", "postgres", "postgres")
model.allTables().foreach { table =>
  if (table.name != "flyway_schema_history") {
    ScalaEntityParser.fromTable(Dialect.PostgreSQL, table, "com.timzaak.dao").writeToFile("./src/main/scala")
  }
}
/*
would output:

package com.timzaak.test
import java.time.OffsetDateTime
                      
case class Ta(
id: Int,
tTinyInt: Option[Short],
tLong: Option[Long],
tBson: Option[Any],
tDate: Option[LocalDate],
tTimestampTz: Option[OffsetDateTime],
tText: Option[String],
tMoney: Option[BigDecimal],
tArrayInt: Option[List[Int]],
tArrayText: Option[List[String]],
)
                      
 */

```

## Known Issue

- SQLite `timestamp` jdbc type would be `VARCHAR`.
