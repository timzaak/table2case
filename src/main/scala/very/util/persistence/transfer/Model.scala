package very.util.persistence.transfer

import very.util.persistence.transfer.wrapper.{ ResultSetIterator, WrappedResultSet }

import java.sql.{ Connection, DriverManager, JDBCType, Types }

class Model(url: String, username: String = null, password: String = null) extends AutoCloseable {

  private val connection = DriverManager.getConnection(url, username, password)

  private def columnName(implicit rs: WrappedResultSet): String =
    rs.string("COLUMN_NAME")

  private def columnDataType(implicit rs: WrappedResultSet): JDBCType =
    JDBCType.valueOf(rs.string("DATA_TYPE").toInt)

  private def isNotNull(implicit rs: WrappedResultSet): Boolean = {
    val isNullable = rs.string("IS_NULLABLE")
    isNullable == "NO" || isNullable == "N"
  }
  Types.ARRAY

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
  ): (String, collection.Seq[String]) = {

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
    val (db, tables) = listAllTables(schema, List("TABLE"))
    tables.flatMap(table(db, schema, _))
  }

  def allViews(schema: String = null): collection.Seq[Table] = {
    val (db, tables) = listAllTables(schema, List("VIEW"))
    tables.flatMap(table(db, schema, _))
  }

  def table(
    databaseProductName: String,
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
        Column(
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
          Table(
            schema = Option(schema),
            name = tableName,
            allColumns = allColumns,
            autoIncrementColumns = allColumns.filter(c => c.isAutoIncrement).distinct,
            primaryKeyColumns = primaryKeys.flatMap { name =>
              allColumns.find(column => column.name == name)
            }
          )
        )
    }
  }

  protected[transfer] def _connection: Connection = connection

  override def close(): Unit = {
    connection.close()
  }
}
