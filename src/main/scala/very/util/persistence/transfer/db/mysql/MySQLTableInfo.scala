package very.util.persistence.transfer.db.mysql

import very.util.persistence.transfer.db.wrapper.ResultSetIterator
import very.util.persistence.transfer.db.{ Dialect, SQLColumn, SQLTable }

import java.sql.Connection
import scala.util.Using

object MySQLTableInfo {

  def getTableInfo(
    tableNames: Seq[String],
    connection: Connection,
    database: String // In MySQL, schema is often the database name
  ): Seq[SQLTable] = {

    tableNames.map { tableName =>
      Using(connection.prepareStatement(s"""
              SELECT
                  COLUMN_NAME AS name,
                  ORDINAL_POSITION AS number,
                  COLUMN_DEFAULT AS default_value,
                  IS_NULLABLE AS is_nullable,
                  DATA_TYPE AS type,
                  COLUMN_TYPE AS full_type, -- Includes length, unsigned, etc.
                  CHARACTER_MAXIMUM_LENGTH AS char_max_length,
                  NUMERIC_PRECISION AS numeric_precision,
                  NUMERIC_SCALE AS numeric_scale,
                  COLUMN_KEY AS column_key, -- PRI, UNI, MUL
                  EXTRA AS extra -- e.g., auto_increment
              FROM information_schema.columns
              WHERE table_schema = ? AND table_name = ?
              ORDER BY ORDINAL_POSITION
              """)) { ps =>
        ps.setString(1, database)
        ps.setString(2, tableName)
        val columnsData = ResultSetIterator(ps.executeQuery()).map { rs =>
          val columnName = rs.string("name")
          val columnExtra = rs.string("extra") // For logging
          val dataType = rs.string("type")
          val isNull = rs.string("is_nullable").toUpperCase == "YES"
          val isAutoIncrement = columnExtra.toLowerCase.contains("auto_increment")
          val isPrimaryKey = rs.string("column_key").toUpperCase == "PRI"
          (SQLColumn(columnName, dataType, !isNull, isAutoIncrement), isPrimaryKey)
        }.toList

        val columns = columnsData.map(_._1)
        val primaryKeys = columnsData.filter(_._2).map(_._1.name)

        SQLTable(
          tableName,
          Dialect.MySql,
          columns,
          primaryKeys,
          schema = Some(database)
        )
      }.get
    }
  }
}
