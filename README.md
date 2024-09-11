## TableToCaseClass

This is used to create case class code from Database info, it would support SQLite, Postgres and MySQL.

It's in early development, all things may change.

The startup code is from ScalikJDBC, this project made some change to support other ORM project.


- [x] transfer to ScalaCaseClass
- [ ] support timestamp
- [x] support ScalaSQL
- [ ] add tests for PostgreSQL and MySQL, add GitHub Actions.

## Known Issue
- SQLite `timestamp` jdbc type would be `VARCHAR`.
- SQLite `primary key` would be nullable.