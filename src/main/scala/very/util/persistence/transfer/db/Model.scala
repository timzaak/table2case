package very.util.persistence.transfer.db

import very.util.persistence.transfer.db.JDBCColumn
import very.util.persistence.transfer.db.mysql.MySQLTableInfo
import very.util.persistence.transfer.db.pg.PGTableInfo
import very.util.persistence.transfer.db.wrapper.{ ResultSetIterator, WrappedResultSet }

import java.sql.{ Connection, DriverManager, JDBCType }

class Model(url: String, username: String = null, password: String = null) extends AutoCloseable {

  private val connection = DriverManager.getConnection(url, username, password)

  lazy val dialect: Dialect = {
    val meta = connection.getMetaData
    val databaseProductName = meta.getDatabaseProductName
    databaseProductName match {
      case "MySQL"      => Dialect.MySql
      case "PostgreSQL" => Dialect.Postgres
      case "SQLite"     => Dialect.Sqlite
      case "H2"         => Dialect.H2
      case _            => Dialect.Sqlite
    }
  }
  private def columnName(implicit rs: WrappedResultSet): String =
    rs.string("COLUMN_NAME")

  private def columnDataType(implicit rs: WrappedResultSet): JDBCType =
    JDBCType.valueOf(rs.string("DATA_TYPE").toInt)

  private def isNotNull(implicit rs: WrappedResultSet): Boolean = {
    val isNullable = rs.string("IS_NULLABLE")
    isNullable == "NO" || isNullable == "N"
  }

  private def isAutoIncrement(db: String, primaryKeys: List[String])(implicit
    rs: WrappedResultSet
  ): Boolean =
    try {
      if (db == "SQLite") {
        columnDataType match {
          case v
            if (v == JDBCType.INTEGER || v == JDBCType.BIGINT) && primaryKeys
              .contains(columnName) =>
            true
          case _ => false
        }
      } else {
        val isAutoIncrement = rs.string("IS_AUTOINCREMENT")
        isAutoIncrement == "YES" || isAutoIncrement == "Y"
      }
    } catch {
      case e: Exception => false
    }

  private def listAllTables(
    schema: String,
    types: List[String]
  ): (String, Seq[String]) = {

    val meta = connection.getMetaData
    val databaseProductName = meta.getDatabaseProductName
    val (catalog, _schema) = {
      (schema, databaseProductName) match {
        case (null, _)           => (null, null)
        case (s, _) if s.isEmpty => (null, null)
        case (s, "MySQL")        => (s, null)
        case (s, _)              => (null, s)
      }
    }

    databaseProductName -> new ResultSetIterator(
      meta.getTables(catalog, _schema, "%", types.toArray)
    ).map { rs =>
      rs.string("TABLE_NAME")
    }.toList

  }

  def allTables(schema: String = null): collection.Seq[Table] = {
    dialect match {
      case Dialect.Postgres =>
        val fixedSchema = if (schema == null) "public" else schema
        PGTableInfo.getTableInfo(connection, fixedSchema).get
      case Dialect.MySql =>
        val (_, tables) = listAllTables(schema, List("TABLE"))
        MySQLTableInfo.getTableInfo(tables, connection, connection.getCatalog)
      case _ =>
        val (db, tables) = listAllTables(schema, List("TABLE"))
        tables.flatMap(table(db, dialect, schema, _))
    }
  }

  def table(
    databaseProductName: String,
    dialect: Dialect,
    schema: String = null,
    tableName: String
  ): Option[Table] = {
    val catalog = null
    val _schema = if (schema == null || schema.isEmpty) null else schema

    val meta = connection.getMetaData
    val primaryKeys = ResultSetIterator(
      meta.getPrimaryKeys(catalog, _schema, tableName)
    ).map(implicit rs => columnName).toList.distinct

    new ResultSetIterator(meta.getColumns(catalog, _schema, tableName, "%"))
      .map { implicit rs =>
        JDBCColumn(
          columnName,
          columnDataType,
          isNotNull,
          isAutoIncrement(databaseProductName, primaryKeys)
        )
      }
      .toList
      .distinct match {
      case Nil        => None
      case allColumns =>
        Some(
          JDBCTable(
            dialect = dialect,
            schema = Option(schema),
            name = tableName,
            allColumns = allColumns,
            primaryKeyColumns = primaryKeys
          )
        )
    }
  }

  protected[transfer] def _connection: Connection = connection

  override def close(): Unit = {
    connection.close()
  }
}
