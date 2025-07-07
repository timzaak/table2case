## TableToCase

[![Maven Central](https://img.shields.io/maven-central/v/com.fornetcode/table2case_3.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.fornetcode/table2case_3)

This is used to create case class code from database table info, it would support SQLite, Postgres and MySQL.

The startup code is from ScalikJDBC, this project made some changes to support another ORM project.

It only supports Scala 3, java17 +, if you want to use it with Scala 2, or lower JVM version, please open an issue
letting me know.

- support scala case class.
- support scalasql.

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

```sbt
libraryDependencies += "com.fornetcode" %% "table2case" % "$VERSION"
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
